# machine learning
# random forest

import pandas as pd

data = pd.read_csv("../data/tmp_data.csv")

# 데이터 전처리
	# 데이터 타입 지정 
gender_dict = {'male':1, 'female':0}
fre_dict = {'Once a month or less':0,
            '2-3 times a month':1,
            '1-2 times a week':2,
            '3-4 times a week':3,
            'Almost every day':4}
dr_place_dict = {'Home':1, 'Other area':0}

# 나이대
# 자이로스코프 변화량
# 위치 변수 (선호 장소, 현재 위치, 주류 소비 장소 -> val)

data["GENDER_N"] = data.gender.map(gender_dict)
data["FREQUENCY_N"] = data.drinking_frequency.map(fre_dict)
data["DRINKING_PLACE_N"] = data.drinking_place.map(dr_place_dict)
