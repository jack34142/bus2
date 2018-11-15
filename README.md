# bus2
桃園公車站牌


## 版本更新

v2.0.0

	1. 新增功能
	  (1) 根據使用者選擇之搭車站、目的地匹配公車
	  (2) 顯示匹配公車的時刻表
	  (3) 時刻表每20秒自動更新
	
	未來計劃：
	  (1) 搜尋功能
	  (2) 我的最愛


v1.1.0

	新增找到自己位置的按鈕


v1.0.0

	hello world!


## 載點

v2.0.0    [google drive 載點](https://drive.google.com/file/d/1D2Fx8AdUsqebIO0NQp5TlNvONxU6zEKz/view?usp=sharing)

v1.1.0　　[google drive 載點](https://drive.google.com/file/d/1BpaNArX1oqmBqNPGCqQfej9VWqUq1C3v/view?usp=sharing)

v1.0.0　　[google drive 載點](https://drive.google.com/file/d/1cYkllY2fPwqCcET_85sY5diz0Mc2KLEp/view?usp=sharing)


## 功能
1. 顯示你的位置
2. 顯示所有的桃園站牌
3. 根據選擇之「搭車站」與「目的地」匹配公車 (可不選)
4. 顯示匹配公車時刻表
5. 時刻表會自動更新 (20秒一次)


### 運用技術

1.	Google map 

	(1)	Google service sdk

	(2)	api申請

	(3)	OnMapReadyCallback介面實作


2.	Volley

	(1)	Volley sdk

	(2)	自製Volley功能整合model


3.	SQLite

	(1)	MyDBHelper的通用類別

	(2)	實作了2個DAO規範的類別

4.	檢測網路功能是否正常的function

5.	FileIO 的2個function

	(1)	讀取

	(2)	寫入

6.	GPS功能

	(1)	LocationUtils的通用類別

	(2)	權限的請求

	(3)	請求的回傳處理

7.	Gson

	(1)	Gson format的使用

	(2)	Gson sdk

8.	了解Json的格式

9.	Thread與Handler

	(1)	Sleep() 與 interrupt() 的應用

	(2)	Handler與Message的應用

	(3)	runOnUiThread應用

10.	Dialog － 自訂視窗

11.	雙擊返回，退出程式

	註1：

		這個專案暫時還沒有使用到fragment、Intent、adapter……等功能，
		這些也都是我熟悉的功能，並不是不會所以沒用，只是還不需要而已。

	註2：
	
		上面有些類別並不是100%我完全獨創的，雖然有些是參考別人的，
		但上面的功能都是在我已經了解其內容了，才做修改與使用，
		我並不是那種單純的使用別人的方法，卻不懂其內部的運作的人。

--------------------------------------------------------------------------

	提醒：
		
		如果你測試時使用的是電腦的虛擬裝置，
		請確定這個裝置能不能接收NETWORK_PROVIDER的gps資訊，
		如果你想要讓他改用GPS_PROVIDER，
		可以修改MainActivity的第150行，
		不過還是建議用手機測試功能。
