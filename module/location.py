# 위치 출력
from geopy.geocoders import Nominatim
import requests
import json

# 좌표 호출
def current_location():
    here_req = requests.get("http://www.geoplugin.net/json.gp")

    if here_req.status_code != 200:
        print("현재 좌표를 불러올 수 없음")
        return None
    else:
        location = json.loads(here_req.text)
        # 딕셔너리 형태로 저장
        crd = {"lat": str(location["geoplugin_latitude"]), "lng": str(location["geoplugin_longitude"])}
        return crd

# 좌표 -> 주소
# 역지오코딩
def geocoding_reverse(lat_lng_str):
    geolocator = Nominatim(user_agent='South Korea')
    location = geolocator.reverse(lat_lng_str, exactly_one=True)
    
    return location.address

# 출력
crd = current_location()
print(crd)

if crd:
    lat_lng_str = f"{crd['lat']}, {crd['lng']}"
    address = geocoding_reverse(lat_lng_str)
    print(address)
else:
    print("좌표를 얻을 수 없습니다.")
