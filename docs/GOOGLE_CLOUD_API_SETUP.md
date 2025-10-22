# ☁️ Google Cloud API Setup

Before you can use Google Maps on Android, you need to register a Google Cloud API project and enable the Maps SDK for Android:

### 1. Register a Google Cloud API project and enable the Maps SDK for Android

- Open your browser to the [Google API Manager](https://console.cloud.google.com/) and create a project
- Once it's created, go to the project and enable the Maps SDK for Android

### 2. Copy your app's SHA-1 certificate fingerprint

**For Google Play Store:**

- Upload your app binary to Google Play Console at least once
- Go to Google Play Console > (your app) > Release > Setup > App integrity > App signing
- Copy the value of SHA-1 certificate fingerprint

**For development builds:**

- After the build is complete, go to your project's dashboard
- Under Project settings > click Credentials
- Under Application Identifiers, click your project's package name
- Under Android Keystore copy the value of SHA-1 Certificate Fingerprint

### 3. Create an API key

- Go to [Google Cloud Credential manager](https://console.cloud.google.com/apis/credentials) and click Create Credentials, then API Key
- In the modal, click Edit API key
- Under Key restrictions > Application restrictions, choose Android apps
- Under Restrict usage to your Android apps, click Add an item
- Add your package name to the package name field
- Then, add the SHA-1 certificate fingerprint's value from step 2
- Click Done and then click Save

### 4. Add the API key to your project

- Copy your API Key into your `AndroidManifest.xml` as shown above
- Create a new build, and you can now use the Google Maps API on Android

