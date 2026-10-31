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

## Android app (embedded/offline edition)
The complete React UI and Bengali fonts are bundled into the APK and displayed with Android WebViewAssetLoader. The app launches directly into its own dashboard; no browser or ChatGPT sign-in is needed.

Records are saved in IndexedDB on this device. This is a personal, single-device edition. App data and the hosted website database are separate and are not synchronized. Website payments, SMS/email integrations and separate teacher/guardian accounts remain unconnected. Use Admin > Complete backup before uninstalling, clearing app data, or switching phones. Restore merges records by ID.

Image/PDF selection, print-to-PDF and backup export use Android's native pickers/print service. External call, map, social and contact links may open the appropriate other app. Startup and all internal pages remain inside this app.

The GitHub Actions workflow first builds the bundled UI, then compiles and lints the APK, verifies its signature and publishes a Release. The intentionally public preview signing key is not a confidential production key. A confidential signing key is required before public app distribution.

Install the updated APK over the previous version. Minimum Android 6 with an up-to-date System WebView. The website still uses its existing private authenticated access.

App display name: **আমাদের মাদরাসা**. Dashboard institution name and campus photo area share a responsive side-by-side panel on mobile and desktop. CSS transform animation runs automatically on touch devices; mouse tilt is a desktop enhancement.
