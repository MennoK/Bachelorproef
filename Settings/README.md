Settings
=====

We zullen 10 verschillende settings gebruiken om de features van log-bestanden te berekenen.
Hiervoor zullen we voor de volgende parameters verschillende waarden nemen, variërend tussen een lage waarde (L), een default waarde (D) en een hoge waarde (H):

1. nb_fft_features
     * L: 10
     * D: 20
     * H: 40

2. nb_fft_peaks
	* L: 0.10
	* D: 0.25
	* H: 0.50

3. wavelet_type: 3 verschillende types
	* haar : default
    * ...
    * ...

4. nb_dwt_features
	* L: 5
	* D: 10
	* H: 20

5. nb_wpd_features
 	* L: 5
	* D: 10
	* H: 20



Verschillende settings:
-----

 Parameter |Setting 1 | Setting 2 | Setting 3 | Setting 4 | Setting 5
:-----------|------------|:------------|:------------|:------------|:------------
 nb_fft_features|        20 |     20 | 20| 10|10     
 nb_fft_peaks   |     0.25 |    0.10 | 0.50 | 0.25 | 0.10  
 wavelet_type   |        haar |     haar   |haar|haar|haar  
nb_dwt_features |          10 | 5    | 20  | 10 | 5
 nb_wpd_features|       10 |    5    |20 | 10 | 5
 
 ---

 Parameter |Setting 6 | Setting 7 | Setting 8 | Setting 9 | Setting 10
:-----------|------------|:------------|:------------|:------------|:------------
 nb_fft_features|        10 |     40|40|40|20     
 nb_fft_peaks   |      0.50 |    0.25|0.10|0.50|0.25  
 wavelet_type   |        haar |     haar  | haar|haar|haar    
nb_dwt_features |          20 |      10 |    5|20|5
 nb_wpd_features|       20 |    10   | 5|20 | 20
 
