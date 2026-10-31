# Dhekuncha Diyol Charmatha Bazar Noorani Hafeziya Madrasa

Private review edition. Bengali emerald/gold glass dashboard with glossy buttons and an illuminated selected-page sidebar with responsive navigation, animated perspective campus photo placeholder, dark/light theme and motion toggle.

## Content
User-supplied name, establishment year 2018, 200 students, 10 teachers, Noorani and Maktab departments, address and contact links are used. Empty content has contextual Bengali prompts. No demo notices, routines, syllabus or results are seeded. The principal's statement is an approval draft. Real photos/logo can be uploaded and changed under admin settings. A 19-item checklist links directly to the relevant editor and tracks content presence. External account/payment/SMS integrations remain explicitly pending; entering setup text does not activate them. No invented email address, payment account or testimonials.

## Working features
- D1-backed create, update and delete for notices, routines, results, students, admission applications, attendance, fees, teachers, gallery, syllabus and settings.
- R2 JPG/PNG/WebP/PDF uploads with 8 MB limit.
- Admission form, residential eligibility check (boys only), tracking ID and status lookup in the private owner session.
- Result lookup and printable marksheet. No sample student result is displayed. Four example subjects, 100 marks each; no pass/grade rules have been assumed.
- Search/filter, attachments, gallery, JSON backup export, real contact and map links.

## Access boundary
This Site is owner-private. Administrative APIs require platform-authenticated identity and rely on the owner-private hosting boundary. There is intentionally no hardcoded password or claim that a demo role switch authenticates anyone. Do not change the site audience to public until an explicit administrator membership model and admission ownership protections are implemented. Student/guardian self-service and teacher role accounts are placeholders, visibly labeled in the UI. The private owner can manage records now.

Public rollout also needs the institution's approval of principal copy, real photos/logo, actual curriculum, grading rules, fees and admissions calendar. Online payments, SMS, email delivery and push notifications are not connected and no charge/sending is simulated.

## Technical
Vinext / React / Cloudflare Workers, D1 binding DB and R2 binding BUCKET. Schema changes must append Drizzle migrations. Deployment applies migrations. Fonts are fetched from Google Fonts, falling back to sans-serif. No ad/analytics tracker is included.

Use the maintained Sites build helper, then the source workflow and private deployment. Keep the existing project_id.

## Android APK
The android/ directory contains a Java Android application that opens the hosted website through Android Custom Tabs, so normal browser login and secure cookies work. It is not an offline native app. Android 6+ and an HTTPS browser are required. The private Site requires the authorized owner’s ChatGPT sign-in.

The GitHub Actions workflow builds and lints the release APK and creates a downloadable GitHub Release on main-branch Android changes. The included preview.keystore is an intentionally public test key (password android), not a production secret. It permits reproducible preview updates; use a confidential release key before public distribution.

Download the APK asset from Releases. Android may ask to allow installation from the downloading browser. No root or developer options are required.
