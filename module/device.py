import time
import board
import busio
import adafruit_mpu6050

# I2C 버스 초기화
i2c = busio.I2C(board.SCL, board.SDA)

# MPU6050 객체 생성
mpu = adafruit_mpu6050.MPU6050(i2c)

# 데이터 수집
while True:
    # 가속도 및 자이로스코프 데이터 읽기
    acceleration = mpu.acceleration
    gyro = mpu.gyro

    # 데이터 출력
    print("가속도 (m/s^2): X={0:.2f}, Y={1:.2f}, Z={2:.2f}".format(*acceleration))
    print("자이로스코프 (rad/s): X={0:.2f}, Y={1:.2f}, Z={2:.2f}".format(*gyro))
    
    # 1초 간격으로 데이터 수집
    time.sleep(1)
