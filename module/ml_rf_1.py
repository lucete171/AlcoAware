# machine learning
# random forest

import pandas as pd
from timestamp import add_timestamp_columns

data = pd.read_csv("../data/tmp_data.csv")

# 1. 데이터 전처리
# 데이터 타입 지정
gender_dict = {'male':1, 'female':0}
fre_dict = {'Once a month or less':0.0333,
            '2-3 times a month':0.0833,
            '1-2 times a week':0.3000,
            '3-4 times a week':0.7000,
            'Almost every day':0.9333}
drinking_map = {'Home':1,
                'Other area':0}
pref_list = ['a', 'b', 'd'] # 이후에 변경

data['AGE_RANGE'] = (data['age']//10) * 10
data["GENDER_N"] = data["gender"].map(gender_dict)
data["FREQUENCY_N"] = data["drinking-frequency"].map(fre_dict)
data['DRINKING_H'] = data['drinking-place'].map(drinking_map)
   
# 자이로스코프 변화량
add_timestamp_columns(data)
data['CUR_LOC'] = (data['residence'] == data['location']).astype(int)
data['CUR_ENT'] = data['location'].isin(pref_list).astype(int)
data = pd.get_dummies(data, columns=['day'], prefix='day') # DAY_N 컬럼 생성(one-hot-encoding)

# data columns 이름 변경
data = data.rename(columns={'day': 'DAY',
                            'time':'TIME',
                            'acceleration':'ACCELERATION',
                            'ziroscope':'ZIROSCOPE',
                            'drunk':'DRUNK'})


# 2. 특성 선정, 데이터 분리
# corrDf = data.corr(numeric_only = True) # 숫자 데이터만 취급
# corrDf = corrDf.drop(['user_id', 'age', 'timestamp'], axis = 1)
# stdCorr = 0.5


# 여기서 DRUNK로 바꿨는데도 drunk 써야만 error가 발생하지 않음 why?
# features = list(corrDf.loc[(abs(corrDf.drunk) > stdCorr) & (abs(corrDf.drunk != 1))].index)

# 이후에 day 관련 data 추가 여부 봐줘야함
features = ['AGE_RANGE',
            'GENDER_N',
            'FREQUENCY_N',
            'ACCELERATION',
            'ZIROSCOPE',
            'DRINKING_H',
            'CUR_LOC',
            'CUR_ENT',
            'TIME',
            'day_Thursday',
            'day_Friday',
            'day_Saturday']
label = ['DRUNK']

# data shuffle
data = data.sample(frac=1).reset_index(drop=True)

stdR = 0.7 # standard Ratio
stdIdx = int(data.shape[0]*stdR)

x_features = data.loc[0:stdIdx, features]
x_label = data.loc[0:stdIdx, label]
y_features = data.loc[stdIdx+1:, features]
y_label = data.loc[stdIdx+1:, label]

# 3. 모델
from sklearn import ensemble
from sklearn.metrics import mean_squared_error as mse

# fit 형식 변환
x_label = x_label.values.ravel()

# 모델 선언
modelRf = ensemble.RandomForestRegressor(random_state = 10)
fittedModelRf = modelRf.fit(x_features, x_label)

# 4. 예측
predictRf = fittedModelRf.predict(y_features)
y_label["RF_PREDICT"] = predictRf
print(y_label)
print("\n")

rfmse= mse(y_label.DRUNK, y_label.RF_PREDICT)
print(rfmse)

# 데이터 정리
