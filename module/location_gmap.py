# google maps
import googlemaps
import requests

gmap_keys = "AIzaSyDpE0KpgjsoYuOUkPi7YVSToNNJftkhpwQ"

def get_current_location(api_key):
    url = f"https://maps.googleapis.com/maps/api/geocode/json?key={api_key}&sensor=false"
    try:
        response = requests.get(url)
        data = response.json()
        if response.status_code == 200 and data['status'] == 'OK':
            # 현재 위치의 주소를 반환
            return data['results'][0]['formatted_address']
        else:
            print("Error fetching current location:", data['error_message'])
            return None
    except Exception as e:
        print("Error fetching current location:", str(e))
        return None

# Google Maps Geocoding API 키

current_location = get_current_location(gmap_keys)
if current_location:
    print("현재 위치:", current_location)
else:
    print("현재 위치를 가져올 수 없습니다.")