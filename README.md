# PETFEET - Find a Paw, Give a Home
*Every Paw Deserves a Home.*

An online pet adoption platform: **Java Servlets + JSP + JDBC**, with **PostgreSQL online** and **MySQL for local development**, built with Maven (WAR), MVC architecture.
Three roles - **Admin**, **Shelter**, **Adopter** - each with a working dashboard.

---------------------------------------------------------------------

## 1. Requirements
| Tool | Version |
|---|---|
| JDK | 11 or newer |
| Apache Tomcat | **9.x** (uses `javax.servlet`; Tomcat 10+ will NOT work) |
| MySQL | 8.x (MariaDB 10.x also works) |
| Maven | 3.6+ |

## 2. Database setup
1. Start MySQL.
2. From the project folder run:
   ```
   mysql -u root -p < database.sql
   ```
   (`database/database.sql` is the same file.) It drops/recreates `petfeet_db`, creates all tables and relationships, and inserts demo users, 13 pets and sample applications.
3. Open `src/main/resources/db.properties` and set your MySQL user/password:
   ```
   db.url=jdbc:mysql://localhost:3306/petfeet_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=YOUR_PASSWORD
   ```
   (You can also override without editing: `-Ddb.url=... -Ddb.user=... -Ddb.password=...` in Tomcat's `JAVA_OPTS`.)

## 3. Build and run
```
mvn clean package
```
Copy `target/PetFeet.war` into Tomcat's `webapps/` folder, start Tomcat, then open
**http://localhost:8080/PetFeet/**

Optional: `mvn cargo:run` downloads Tomcat 9 and runs the app for you (needs internet).
In Eclipse / IntelliJ / NetBeans: import as a Maven project and run on a Tomcat 9 server.

## 4. Demo accounts  (DEMO PASSWORD for all: `Demo@123`)
| Role | Email |
|---|---|
| Admin | admin@petfeet.com |
| Shelter | shelter@petfeet.com (also paws@petfeet.com, care@petfeet.com) |
| Adopter | adopter@petfeet.com (also meera@petfeet.com, kabir@petfeet.com) |

Passwords are stored as salted PBKDF2-HMAC-SHA256 hashes, never as plain text. Change the demo passwords before any real use.

## 5. Suggested demo flow (viva / presentation)
1. Open the landing page, search pets, tick **Compare** on two pets, open a pet page.
2. Log in as **adopter** -> favourite a pet -> **Adopt Me** -> submit the form -> *Application submitted successfully!* -> track it in **My Applications**.
3. Log in as **shelter** -> **Applications** -> **Approve** (this runs the JDBC transaction) -> *Mark completed*. Add a new pet.
4. Log in as **admin** -> **Pet Listings** -> approve the new pet. Show **Background Services**, **Analytics**, **User Management**, **System Settings**, **Activity Logs**.
5. Open **About Project / Java Concepts** (footer link, `/about-project`) and walk through the checklist.

## 6. Project structure
```
PetFeet/
├── pom.xml
├── database.sql
├── README.md
└── src/main
    ├── java/com/petfeet
    │   ├── model/      User (abstract) -> Admin, Shelter, Adopter; Pet, AdoptionApplication, Message, Notification ...
    │   ├── dao/        CRUDOperations<T,ID>, BaseDAO, UserDAO, PetDAO, ApplicationDAO, MessageDAO, NotificationDAO ...
    │   ├── service/    business rules, transactions, background workers
    │   ├── servlet/    28 controller servlets (BaseServlet is the parent)
    │   ├── filter/     AuthFilter (session + role), CsrfFilter
    │   ├── listener/   AppLifecycleListener (starts/stops threads)
    │   ├── util/       DBConnection, PasswordUtil, ValidationUtil, WebUtil, PetFormParser
    │   └── exception/  PetFeetException hierarchy
    ├── resources/db.properties
    └── webapp
        ├── index.jsp, css/, js/, images/, uploads/
        └── WEB-INF/  web.xml, tags/ (reusable JSP tags), views/{public,common,admin,shelter,adopter}
```
JSP views live under `WEB-INF/views`, so they cannot be opened directly - every page goes through a servlet (controller).

## 7. MVC architecture
* **Model** - `model` (entities), `dao` (database access), `service` (business rules).
* **View** - JSP + JSTL in `WEB-INF/views`, with tag files (`petCard`, `badge`, `chart`, `tracker`, ...).
* **Controller** - servlets: read the request, call a service, put the result in the request, forward to a JSP (or redirect after POST).

Flow: `Browser -> CsrfFilter/AuthFilter -> Servlet -> Service -> DAO -> MySQL`, then `Servlet -> JSP -> Browser`.

## 8. Java concepts and where they are used
| Concept | Where |
|---|---|
| Classes/objects, constructors, encapsulation | all `model` classes (private fields, getters/setters) |
| Inheritance | `User -> Admin/Shelter/Adopter`; `BaseServlet`; `BaseDAO`; `PetFeetException` subclasses |
| Polymorphism | `UserDAO` returns the right `User` subclass; `NotificationService` implementations (`DbNotificationService`, `QueuedNotificationService`) |
| Abstraction | abstract `User`, `BaseDAO`, `BaseServlet` |
| Interfaces | `CRUDOperations<T,ID>`, `NotificationService`, `MessageService`, `RowMapper<T>`, `Runnable` |
| Method overriding | `getRole()`, `getDashboardPath()`, `getCapabilities()`, `toString()`, servlet `init/service/destroy/doGet/doPost` |
| Exception handling | `com.petfeet.exception`, try-with-resources, `BaseServlet.friendlyMessage()`, error page in `web.xml` |
| Collections / Generics | `List<Pet>`, `Set<Integer>` favourites, `Map<String,Long>` statistics, `Queue` in `NotificationQueue`, generic `queryList<T>` |
| Multithreading | `NotificationWorker`, `StatisticsWorker` (both `Runnable`), started by `AppLifecycleListener` |
| Synchronization | `NotificationQueue` (`synchronized`, `wait/notifyAll`), `StatsCache`, `BackgroundStatus`, `AtomicLong`, `volatile` |
| JDBC / PreparedStatement | `DBConnection` (DriverManager), `BaseDAO` (Connection, PreparedStatement, ResultSet) - every query is parameterised |
| CRUD | users (admin), pets (shelter), messages, notifications, favourites |
| Transactions | `AdoptionService.approve()` / `reject()` |
| Servlets, session | `LoginServlet`, `LogoutServlet`, `AuthFilter`, `HttpSession` |

## 9. JDBC operations
`BaseDAO` provides `queryList`, `queryOne`, `count`, `groupCount`, `update`, `insert`. They open a `Connection` with `DBConnection.getConnection()`, prepare the SQL, bind parameters with `setObject`, run it, map `ResultSet` rows through a `RowMapper<T>` lambda and close everything automatically (try-with-resources). `SQLException` is wrapped in `DatabaseException`, so users only ever see a friendly message while the real error goes to the Tomcat log.

## 10. Transaction management (`AdoptionService.approve`)
```
connection.setAutoCommit(false)
 1. UPDATE adoption_applications -> APPROVED
 2. UPDATE pets                  -> ADOPTED
 3. INSERT adoption_history
 4. INSERT notifications (winner + other applicants who lost out) and reject other pending applications
connection.commit()            // all steps succeed together
catch -> connection.rollback() // or none of them happen
```
Tested by temporarily renaming `adoption_history`: the approval failed with a friendly message and both the application and the pet stayed unchanged.

## 11. Multithreading and synchronization
* `NotificationWorker` (thread 1) waits on a shared `NotificationQueue`. Request threads add notifications (new application, new message, listing approved...) and return immediately; the worker writes them to MySQL.
* `StatisticsWorker` (thread 2) recalculates admin statistics every 15 seconds into a `StatsCache`.
* Shared state is protected with `synchronized` methods and `wait()/notifyAll()`.
* The admin dashboard -> **Background services** panel shows both services' status, counters and last execution time.

## 12. Servlet lifecycle and session management
* `BaseServlet` logs `init()` and `destroy()` and counts requests in `service()`.
* Login: credentials checked -> session id regenerated -> user id, role and user object stored -> redirect by role. Session timeout 30 minutes.
* `AuthFilter` sends anonymous visitors to the login page and returns **403** for a wrong role (e.g. an adopter opening `/admin/...`).
* Logout invalidates the session. Private pages are sent with `Cache-Control: no-store`.
* `CsrfFilter` requires a per-session token on every POST.

## 13. Features checklist
Registration (Adopter/Shelter only), login (show/hide password, remember-me e-mail cookie), role dashboards with charts, admin CRUD for users, pet approval, system settings, activity logs, shelter pet CRUD with photo upload, application review, adopter search/filter/apply/track, messaging with unread counts, notifications bell, favourites, pet comparison, "Pets you may love" (rule-based), application progress tracker, search suggestions, empty states, loading indicators, responsive layout, custom "Oops! This paw went missing." page, and the **About Project / Java Concepts** viva page.

## 14. Honest notes / limitations
* **Forgot password** is a UI only: there is no e-mail server, so the dialog tells users to ask an admin to reset it (Admin -> User Management -> Edit).
* **Remember me** only remembers the e-mail address on the login form (it does not keep you logged in).
* Online pet photos are stored in PostgreSQL and survive container restarts (2 MB per upload). Local MySQL runs save uploads in the deployed `uploads/` folder (5 MB per upload), which must be backed up before replacing a local WAR.
* Pet pictures that ship with the demo data are drawn illustrations (SVG), not photographs. Google Fonts are loaded from the internet (system fonts are used as fallback).
* `max_active_applications`, `allow_registration` and `require_pet_approval` settings are enforced by the code.
* There is no payment, e-mail or SMS integration.

---

## Online deployment (Render)

This repository is deployment-ready for a public Render web service while preserving the original local MySQL workflow.

### Architecture

- **Local:** Tomcat 9 + MySQL (`db.properties` / `DB_*` overrides)
- **Online:** Docker + Tomcat 9 + PostgreSQL (`DATABASE_URL`)
- The online app uses the PostgreSQL schema **`petfeet`**, so it can safely share a PostgreSQL server with another project without sharing tables.
- On first hosted startup, PetFeet automatically creates and seeds its own schema from `src/main/resources/postgres.sql`. A version marker prevents later restarts from re-creating deleted demo records or resetting favorites.
- Uploaded pet photos are stored as data URLs in PostgreSQL in hosted mode, so they do not disappear when a Render container restarts.

### Required Render settings

Create a new **Web Service** from this repository and choose **Docker**. Use:

- Region: `Virginia` if sharing the existing Virginia PostgreSQL database
- Health check: `/health`
- Environment variable: `PETFEET_DB_SCHEMA=petfeet`
- Environment variable: `DATABASE_URL` linked to your PostgreSQL database's internal connection URL

Do **not** put the PostgreSQL password or connection URL into GitHub.

### Local MySQL

The source repository intentionally does not contain a real MySQL password. Supply it locally with either:

```powershell
$env:DB_PASSWORD="your-mysql-password"
mvn cargo:run
```

or:

```powershell
mvn -Ddb.password="your-mysql-password" cargo:run
```

Then open `http://localhost:8080/PetFeet/`.

### Demo logins

The initial hosted seed uses the same demo accounts as the original database script. The demo password is `Demo@123`.

### Deployment checks

The GitHub Actions workflow builds the Docker image, starts it on a custom port with a temporary PostgreSQL database, checks public pages and all three role dashboards, and verifies that saved changes and deleted favorites survive an app restart. It uses disposable test data only.

### Free hosting limits

Render's free web service can sleep when idle, so the first visit may take longer. Free Render PostgreSQL databases expire after 30 days; arrange a database migration or paid plan before the database expires. See [Render's free hosting documentation](https://render.com/docs/free).
