const path = require('path');

require('dotenv').config({ path: path.resolve(__dirname, '..', '.env') });

const admin = require('firebase-admin');

const serviceAccountPath = process.env.GOOGLE_APPLICATION_CREDENTIALS;

if (!serviceAccountPath) {
  throw new Error('GOOGLE_APPLICATION_CREDENTIALS 환경 변수가 설정되지 않았습니다.');
}

const resolvedPath = path.resolve(__dirname, '..', serviceAccountPath);
const serviceAccount = require(resolvedPath);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://alcoaware-cfbcb-default-rtdb.firebaseio.com'
});

const db = admin.database();
module.exports = db;
