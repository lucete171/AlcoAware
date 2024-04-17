import time
import datetime

def process_time(time):
    now = datetime.datetime.fromtimestamp(time)  # 타임스탬프를 datetime 객체로 변환
    weekday = now.strftime("%A")  # 요일 출력

    # 시간 및 분 가져오기
    hour = now.hour
    minute = now.minute

    # 분 반올림
    # 분이 30분 이상이면 시간에 1시간 추가
    if minute >= 30:
        hour += 1

    # 딕셔너리 형태로 저장
    dic = {'weekday': weekday, 'hour': hour}
    return dic['weekday'], dic['hour']  # 요일과 시간 반환

def add_timestamp_columns(data):
    day_list = []
    time_list = []
    for timestamp in data['timestamp']:
        day, hour = process_time(timestamp)
        day_list.append(day)
        time_list.append(hour)
    data['day'] = day_list
    data['time'] = time_list
