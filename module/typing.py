import time
import difflib

# 타이핑 속도 구하기
def calculate_typing_speed(text, elapsed_time):
    words = text.split()
    num_words = len(words)
    minutes = elapsed_time / 60
    speed = num_words / minutes
    return speed

#
def calculate_typo_rate(original_text, input_text):
    diff = difflib.ndiff(original_text, input_text)
    typo_count = sum(1 for d in diff if d[0] != ' ')
    text_length = len(original_text)
    typo_rate = (typo_count / text_length) * 100
    return typo_rate

def check_for_drunk_typing(speed, typo_rate):
    if speed < 10 or speed > 100 or typo_rate > 2:  # 타이핑 속도 및 오타율 기준 설정
        return True
    else:
        return False

def main():
		# 아래 참조
    original_text = "오늘은 햇살 가득한 따뜻한 날씨입니다."

    print("다음 문장을 입력하세요: '{}'".format(original_text))
    input_text = input()

    print("입력을 시작하세요...")
    input("입력이 완료되면 엔터 키를 눌러주세요.")

    start_time = time.time()

    input("입력이 완료되었습니다. 엔터 키를 눌러주세요.")

    end_time = time.time()

    elapsed_time = end_time - start_time
    typing_speed = calculate_typing_speed(input_text, elapsed_time)
    typo_rate = calculate_typo_rate(original_text, input_text)

    print("당신의 타이핑 속도는 {:.2f} 단어/분 입니다.".format(typing_speed))
    print("오타율은 {:.2f}% 입니다.".format(typo_rate))

    if check_for_drunk_typing(typing_speed, typo_rate):
        print("술에 취한 상태일 수 있습니다.")
    else:
        print("술에 취하지 않은 상태입니다.")

if __name__ == "__main__":
    main()
