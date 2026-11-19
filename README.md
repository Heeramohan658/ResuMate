# ResuMate 25 Templates — No MySQL Edition

**Developer:** Heera Mohan Sardar

A complete Java 17 + Spring Boot 3.4.5 resume builder with 25 templates, live preview, editable content, PDF export, heuristic ATS analysis, optional Gemini AI coaching, and local file-based H2 persistence. **MySQL is not used or required.**

## Requirements
- Java 17 or newer
- Maven 3.8+
- Internet connection only for downloading Maven dependencies and optional Gemini API requests

## Run on Windows
1. Extract `ResuMate_25_Templates_No_MySQL.zip`.
2. Open PowerShell in the extracted project folder (the folder containing `pom.xml`).
3. Run:
   ```powershell
   mvn clean spring-boot:run
   ```
4. Open http://localhost:8080

If port 8080 is busy:
```powershell
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```
Then open http://localhost:8081

## Gemini AI setup (optional)
The ATS keyword/section analysis works without an API key. To enable Gemini suggestions, create a Gemini API key and set it in the terminal before starting the app:
```powershell
$env:GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
mvn spring-boot:run
```
Do not paste your API key into public code or share it. Gemini calls require network access and a valid Google AI Studio key/model access. The default model is `gemini-2.0-flash`; change `gemini.model` in `src/main/resources/application.properties` if your account uses another supported model.

## Data storage
Resume records are stored locally in `./data/resumate.mv.db` using H2 file database. No MySQL server, MySQL Workbench, schema import, or external database setup is needed. Back up the `data` folder to back up saved resumes.

H2 console (development only): http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/resumate`
- User: `sa`
- Password: leave blank

## Included features
- 25 resume templates and selectable accent color / font preference controls
- Live preview while editing
- Save, load and delete resumes locally
- PDF export
- ATS score with keyword overlap and section completeness
- Missing keyword list and practical suggestions
- Optional Gemini AI suggestions; ATS analysis remains functional without Gemini

## ATS note
The ATS score is a transparent heuristic, not a certified score and not a guarantee of selection. Tailor your resume truthfully; do not add keywords for skills or experience you do not have.

## API
- `GET /api/resumes`
- `GET /api/resumes/{id}`
- `POST /api/resumes`
- `PUT /api/resumes/{id}`
- `DELETE /api/resumes/{id}`
- `POST /api/ats/analyze` with `{ "resume": { ... }, "jobDescription": "..." }`
- `POST /api/ai/suggestions` with the same request shape
- `POST /api/resumes/pdf` with a resume JSON body
