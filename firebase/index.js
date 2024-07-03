const db = require('./firebase');
const createCsvWriter = require('csv-writer').createObjectCsvWriter;
const fs = require('fs');

const csvFilePath = './data.csv';
const lastTimestampFilePath = 'lastTimestamp.txt';

// CSV Writer 설정
const csvWriter = createCsvWriter({
  path: csvFilePath,
  header: [
    {id: 'user_id', title: 'USER_ID'},
    {id: 'name', title: 'NAME'},
    {id: 'age', title: 'AGE'},
    {id: 'gender', title: 'GENDER'},
    {id: 'region', title: 'REGION'},
    {id: 'drinking_frequency', title: 'DRINKING_FREQUENCY'},
    {id: 'drinking_location', title: 'DRINKING_LOCATION'},
    {id: 'acc_x', title: 'ACC_X'},
    {id: 'acc_y', title: 'ACC_Y'},
    {id: 'acc_z', title: 'ACC_Z'},
    {id: 'gyr_x', title: 'GYR_X'},
    {id: 'gyr_y', title: 'GYR_Y'},
    {id: 'gyr_z', title: 'GYR_Z'},
    {id: 'location', title: 'LOCATION'},
    {id: 'timestamp', title: 'TIMESTAMP'},
    {id: 'drinking', title: 'DRINKING'}
  ],
  append: true  // 기존 파일에 추가
});

// 마지막으로 기록된 타임스탬프를 읽기
function getLastTimestamp() {
  if (fs.existsSync(lastTimestampFilePath)) {
    return fs.readFileSync(lastTimestampFilePath, 'utf8');
  }
  return '0';
}

// 마지막 타임스탬프 저장
function saveLastTimestamp(timestamp) {
  fs.writeFileSync(lastTimestampFilePath, timestamp, 'utf8');
}

// Firebase 데이터베이스 리스너 설정
const ref = db.ref('users');

console.log('Firebase 데이터베이스 리스너 설정 중...');

ref.orderByChild('timestamp').startAt(getLastTimestamp()).on('child_added', (snapshot) => {
  console.log('새로운 데이터 감지: ', snapshot.val());

  const user = snapshot.val();
  const record = {
    user_id: snapshot.key,
    name: user.name,
    age: user.age,
    gender: user.gender,
    region: user.region,
    drinking_frequency: user['drinkingFrequency'],
    drinking_location: user['drinkingLocation'],
    acc_x: user['acc-x'],
    acc_y: user['acc-y'],
    acc_z: user['acc-z'],
    gyr_x: user['gyr-x'],
    gyr_y: user['gyr-y'],
    gyr_z: user['gyr-z'],
    location: user.location,
    timestamp: user.timestamp,
    drinking: user.drinking
  };

  // CSV 파일에 데이터 쓰기
  csvWriter.writeRecords([record])
    .then(() => {
      console.log('CSV 파일이 성공적으로 업데이트되었습니다.');
      saveLastTimestamp(user.timestamp);
    })
    .catch(err => {
      console.error('CSV 파일 업데이트 중 오류 발생:', err);
    });
});

console.log('Firebase 데이터베이스 리스너가 설정되었습니다.');

