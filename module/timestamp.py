import time
import datetime

def process_time():
    timestamp = time.time() # 현재 타임스탬프 호출

    now = datetime.datetime.fromtimestamp(timestamp) # 타임스탬프를 datetime 객체로 변환

    weekday = now.strftime("%A") # 요일 출력

    # 시간 및 분 가져오기
    hour = now.hour
    minute = now.minute
    
    # 분 반올림
    # 분이 30분 이상이면 시간에 1시간 추가
    if minute >= 30:
        hour += 1
        
    # 딕셔너리 형태로 저장
    dic = {'weekday': weekday, 'hour': hour}
    print(dic['weekday'], dic['hour'])

process_time()