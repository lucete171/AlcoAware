import time
import math
from android import Android

# 안드로이드 클래스 초기화
android = Android()

# 음주 가능성 분석
def analyze_intoxication(acceleration, gyroscope):
    # 가속도 _ 평균
    # abs로 변경해야 하나?
    avg_acceleration = sum(acceleration) / len(acceleration)
    # 자이로스코프 _ 평균
    avg_gyroscope = sum(gyroscope) / len(gyroscope)
    
    # threshold 설정
    acceleration_threshold = 1.5
    gyroscope_threshold = 1.0
    
    if (avg_acceleration > acceleration_threshold) and (avg_gyroscope > gyroscope_threshold):
        return 1 # 음주 True
    else:
        return 0 # 음주 False


def main_loop():
    while True:
        # 센서 데이터 호출
        acceleration = android.get_acceleration()
        gyroscope = android.get_gyroscope()

        # 분석
        intoxication_result = analyze_intoxication(acceleration, gyroscope)

        # 결과를 출력
        # 이후 data에 저장하는 코드로 수정
        print("Intoxication Level:", intoxication_result)

        # 1초 대기
        time.sleep(1)

# 이 후 수정 필요
# 맞는지 모름 백그라운드 프로세스 처음
# GPT 화이팅?
class BackgroundService:
    def __init__(self):
        pass

    def start_service(self):
        main_loop()

if __name__ == '__main__':
    service = BackgroundService()
    service.start_service()
