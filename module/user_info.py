import random # 나중에 없애기

# 나이대 계산기
def age_range(age):
    group = int(age / 10) * 10
    return group

# 성별 처리
def gender_convert(str):
    if str == "male":
        return 1
    else: 
        return 0

# 빈도 처리
def fre_convert(str):
    if str == "Once a month or less":
        return 0
    elif str == "2-3 times a month":
        return 1
    elif str == "1-2 times a week":
        return 2
    elif str == "3-4 times a week":
        return 3
    else:
        return 4 # "Almost every day"
    
# 선호 장소 처리
def place_convert(str):
    if(str == "Home"):
        return 1 # 집, 집 근처
    else:
        return 0 # 외부

# 프론트엔드에서 받은 정보를 딕셔너리 형태로 저장
# 프론트엔드에서 받아온 정보를 저장해야 함
user_info = {
    "Age": random.randint(0, 100),
    "Gender": "female",  # 남자: 1, 여자: 0
    "Drinking-Frequency": "3-4 times a week", # 5가지 종류의 음주 빈도 유형
    "Residence": "서울특별시 양천구 목5동",  # 사용자 거주지(시, 구, 동)
    "Drinking Place": "Other area"  # 집: 1, 외부: 0
}

# 사용자 정보 전처리 함수
def process_user_info(data):
    data["Age"] = age_range(data["Age"])
    data["Drinking-Frequency"] = fre_convert(data["Drinking-Frequency"])
    data["Drinking Place"] = place_convert(data["Drinking Place"])
    data["Gender"] = gender_convert(data["Gender"])

# 처리 전 정보
print("처리 전, 사용자 정보:", user_info)

process_user_info(user_info)

# 처리 후 정보
print("처리 후, 사용자 정보:", user_info)
