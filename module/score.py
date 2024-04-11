import numpy as np

# 각각의 모듈에서 score를 호출

# 호출한 score를 리스트로 초기화 및 생성
score_1 = 0 #형식 정해줘야함?###########
score_2 = 0 # 마찬가지#################

score_1 = [s_device, s_timestamp, s_loc, s_age]
score_2 = score_1 + [s_typing]
# score np.array로 변환
score_1 = np.array(score_1)
score_2 = np.array(score_2)

# 모듈에 대한 가중치 설정
weight = np.array([w_d, w_t, w_l, w_a, w_t])

# 가중치를 부여한 총 score 초기화 및 도출
# np.dot 안 써도 될 듯? 안 써도 되나?
t_score_1 = 0 #################
t_score_2 = 0 #################

t_score_1 = score_1 * weight[:4]
t_score_2 = score_2 * weight

# 음주라고 판단되는 기준치(th=threshold)를 호출
# 학습된 데이터로 도출
# 1차나 2차의 기준치에서 동일한 값을 사용
th = ?

# 몇 번째 판단인지 호출
t_i = ?

# 1차 판단
if(t_i = 1)
	if(t_score_1 >= th) # 1차 판단 양성
		alarm
		t_i = 2 #2차 판단으로 이동


# 2차 판단
elif(t_i = 2)
	if(t_score_2 >= th) #2차 판단 양성
		alarm # 경고 메시지 전송
	else
		#relearns