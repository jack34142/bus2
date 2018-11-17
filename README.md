# bus2
桃園公車站牌


## 版本更新

v2.1.4

	1. 新增功能
	　(1) 搜尋系統
	　　i. 以公車站牌搜尋
	　　ii. 以地址搜尋
	　　iii. 以公車站牌搜尋，可以長按箭號，顯示清單

	未來計劃
	　(1) 我的最愛
	　(2) 查看公車抵達每一站的時間表


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

v2.1.4　　[google drive 載點](https://drive.google.com/file/d/1KXq-_sxq5utEyHN_YL7xIdWNRKmDqlMF/view?usp=sharing)


## 功能簡介
1. 自動定位使用者位置
2. 地圖移動到使用者位置的按鈕
3. 顯示所有的桃園站牌
4. 搜尋系統
5. 根據選擇的「搭車站」與「目的地」匹配公車 (可不選)
6. 根據匹配公車，提供到站時間列表
7. 每20秒自動更新時刻表
8. 手動更新時刻表按鈕


### 運用技術

1. google map

	(1) google service sdk

	(2) api申請

	(3) OnMapReadyCallback 介面實作

	(4) mark 

	(5) 搜尋地址的方法


2. SQLite

	(1) MyDBHelper 通用類別

	(2) 了解DAO規範，並實作類別


3. gps

	(1) LocationUtils 通用類別

	(2) 手機權限的請求

	(3) 請求結果的處理


4. Volley  -- 下載資料用的工具

	(1) volley sdk

	(2) 自製volley功能整合的model


5. Gson – 解析 json 用的工具

	(1) Gson SDK

	(2) Gson format 

	　└ 幫助 json 資料製作 bean class


6. RecyclerView

	(1) RecyclerView.Adapter

	(2) LinearLayoutManager – 直列堆疊

	(3) addItemDecoration – 分割線


7. Thread

	(1) sleep()、interrupt()

	(2) Handler、Message、runOnUiThread


8. Intent

	(1) startActivity

	(2) Bundle

	(3) intent.putExtra

	(4) 為新的activity設置返回鍵


9. Fragment

	(1) FragmentStatePagerAdapter

	(2) tabLayout、ViewPager 關聯


10. 其他

	(1) 包裝FileIO讀寫方法

	(2) 檢測網路功能的方法

	(3) 關閉鍵盤的方法

	(4) MyConfig類別 

	　└ 用來放一些系統常數

	(5) 雙擊返回鍵退出應用
