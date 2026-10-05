"""Exercise only the disposable CI database; never run these writes on a live site."""
import base64
import http.cookiejar
import json
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

BASE = "http://127.0.0.1:10000"
PNG = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a5N8AAAAASUVORK5CYII="


class Client:
    def __init__(self):
        self.opener = urllib.request.build_opener(
            urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
        self.csrf = None

    def request(self, path, data=None, headers=None, expected=200):
        request = urllib.request.Request(BASE + path, data=data, headers=headers or {})
        try:
            response = self.opener.open(request, timeout=30)
        except urllib.error.HTTPError as error:
            response = error
        body = response.read().decode("utf-8")
        assert response.status == expected, (path, response.status, body[-1200:])
        token = re.search(r'name="_csrf" value="([^"]+)"', body)
        if token:
            self.csrf = token.group(1)
        return body, response.url

    def post(self, path, fields, expected=200):
        values = dict(fields)
        values.setdefault("_csrf", self.csrf)
        return self.request(path, urllib.parse.urlencode(values).encode(), expected=expected)

    def login(self, email, role):
        self.request("/login")
        assert self.csrf, "Login page has no CSRF token"
        body, url = self.post("/login", {"email": email, "password": "Demo@123"})
        assert urllib.parse.urlparse(url).path == "/" + role + "/dashboard", url
        return body

    def upload_pet(self):
        self.request("/shelter/pets/add")
        boundary = "PetFeetDeploymentTestBoundary"
        fields = {"name": "Persistence Test Pet", "species": "Dog", "breed": "Indie",
                  "age": "2", "gender": "Female", "location": "Test City",
                  "description": "Disposable CI upload", "imageUrl": ""}
        chunks = []
        for name, value in fields.items():
            chunks.append(("--" + boundary + '\r\nContent-Disposition: form-data; name="'
                           + name + '"\r\n\r\n' + value + "\r\n").encode())
        chunks.append(("--" + boundary + '\r\nContent-Disposition: form-data; name="imageFile"; '
                       'filename="pet.png"\r\nContent-Type: image/png\r\n\r\n').encode()
                      + base64.b64decode(PNG) + b"\r\n")
        chunks.append(("--" + boundary + "--\r\n").encode())
        body, url = self.request("/shelter/pets/add?_csrf=" + self.csrf, b"".join(chunks),
                                 {"Content-Type": "multipart/form-data; boundary=" + boundary})
        assert urllib.parse.urlparse(url).path == "/shelter/pets", url
        assert "Persistence Test Pet" in body and "data:image/png;base64," + PNG in body


def wait_ready():
    for _ in range(60):
        try:
            body, _ = Client().request("/health")
            if json.loads(body) == {"status": "ok"}:
                return
        except (AssertionError, urllib.error.URLError, TimeoutError, OSError):
            pass
        time.sleep(2)
    raise AssertionError("PetFeet did not become healthy within two minutes")


def before_restart():
    visitor = Client()
    for path in ["/", "/pets", "/pet?id=1", "/shelters", "/about", "/contact", "/about-project", "/register"]:
        visitor.request(path)
    body, _ = visitor.request("/pets?q=bruno")
    assert 'href="/pet?id=1"' in body, "Lowercase search missed Bruno"
    _, url = visitor.request("/admin/dashboard")
    assert urllib.parse.urlparse(url).path == "/login", "Anonymous access was not rejected"

    clients = {}
    routes = {
        "admin": ["/admin/users", "/admin/pets", "/admin/applications", "/admin/analytics", "/admin/settings", "/admin/logs", "/profile", "/notifications"],
        "shelter": ["/shelter/pets", "/shelter/pets/add", "/shelter/applications", "/messages", "/profile", "/notifications"],
        "adopter": ["/adopter/favorites", "/adopter/applications", "/adopter/apply?petId=2", "/messages", "/profile", "/notifications"]}
    for role, paths in routes.items():
        client = clients[role] = Client()
        client.login(role + "@petfeet.com", role)
        for path in paths:
            client.request(path)
    adopter = clients["adopter"]
    adopter.request("/admin/dashboard", expected=403)
    adopter.post("/favorite", {"petId": "1", "_csrf": "wrong"}, expected=403)
    body, _ = adopter.post("/favorite", {"petId": "1"})
    assert json.loads(body)["favorite"] is False
    body, _ = adopter.post("/profile", {"name": "Aarav Sharma", "phone": "9000000005",
                                       "address": "Test Address", "city": "Persistence Test City",
                                       "preferredSpecies": "Dog", "preferredMaxAge": "5"})
    assert "Profile updated successfully." in body

    new_user = Client()
    new_user.request("/register")
    body, url = new_user.post("/register", {"name": "Deployment Test", "email": "smoke@petfeet.test",
                                           "phone": "9000000010", "role": "ADOPTER",
                                           "password": "Demo@123", "confirmPassword": "Demo@123"})
    assert urllib.parse.urlparse(url).path == "/login" and "Account created!" in body
    new_user.login("smoke@petfeet.test", "adopter")
    _, url = new_user.post("/adopter/apply", {"petId": "2", "applicantName": "Deployment Test",
                                            "phone": "9000000010", "email": "smoke@petfeet.test",
                                            "address": "Test Address", "reason": "A loving permanent home.",
                                            "experience": "Experienced", "homeType": "Apartment", "message": "CI test"})
    application_id = urllib.parse.parse_qs(urllib.parse.urlparse(url).query).get("success", [None])[0]
    assert application_id, "Adoption application did not save"
    body, _ = clients["shelter"].post("/shelter/applications", {"id": application_id, "action": "approve"})
    assert "Application #" + application_id + " approved." in body
    clients["shelter"].upload_pet()
    print("PASS: public pages, all roles, access guards, registration, adoption, photo upload and profile writes")


def after_restart():
    adopter = Client()
    adopter.login("adopter@petfeet.com", "adopter")
    body, _ = adopter.request("/profile")
    assert "Persistence Test City" in body, "Profile was reset after restart"
    body, _ = adopter.request("/adopter/favorites")
    assert 'href="/pet?id=1"' not in body, "Deleted favorite was re-seeded after restart"
    new_user = Client()
    new_user.login("smoke@petfeet.test", "adopter")
    body, _ = new_user.request("/adopter/applications")
    assert "Luna" in body and "Approved" in body, "Adoption was lost after restart"
    shelter = Client()
    shelter.login("shelter@petfeet.com", "shelter")
    body, _ = shelter.request("/shelter/pets")
    assert "Persistence Test Pet" in body and "data:image/png;base64," + PNG in body
    print("PASS: profiles, deleted favorites, new accounts, adoption and uploaded photos survived restart")


if __name__ == "__main__":
    assert len(sys.argv) == 2 and sys.argv[1] in ("before", "after")
    wait_ready()
    (before_restart if sys.argv[1] == "before" else after_restart)()
