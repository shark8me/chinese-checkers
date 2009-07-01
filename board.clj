(def board
{
1  {:ne 3 :nw 2 }
2  {:ne 5 :nw 4 :se 1 :e 3}
3  {:ne 6 :nw 5 :sw 1 :w 2}
4  {:ne 8 :nw 7 :se 2 :e 5}
5  {:e 6 :ne  9 :nw 8 :w 4 :sw 2 :se 3}
6  {:ne 10 :nw 9 :sw 3 :w 5}
7  {:ne 16 :nw 15 :se 4 :e 8}
8  {:e 9 :ne 17 :nw 16 :w 7 :sw 4 :se 5}
9  {:e 10 :ne  18 :nw 17 :w 8 :sw 5 :se 6}
10  {:ne 19 :nw 18 :sw 6 :w 9}
11 { :e  12  :ne  24  } 
12 { :e  13  :w  11  :nw  24  :ne  25  } 
 13 { :e  14  :w  12  :nw  25  :ne  26  } 
 14 { :e  15  :w  13  :nw  26  :ne  27  } 
 15 { :e  16  :ne  28 :nw  27  :w  14 :se 7  } 
 16 { :e  17  :ne  29 :nw  28 :w  15 :sw 7 :se 8  } 
 17 { :e  18  :ne  30 :nw  29  :w  16  :sw 8 :se 9 } 
 18 { :e  19  :ne  31 :nw  30 :w 17 :sw 9 :se 10} 
 19 { :e  20  :ne  32  :nw  31  :w  18  :sw 10 } 
 20 { :e  21  :w  19  :nw  32  :ne  33  } 
 21 { :e  22  :w  20  :nw  33  :ne  34  } 
 22 { :e  23  :w  21  :nw  34  :ne  35  } 
 23 { :w  22  :nw  35 } 
24 { :e  25  :ne  36  :sw  11  :se  12  } 
25 { :e  26  :ne  37  :nw  36  :w  24  :sw  12  :se  13  } 
 26 { :e  27  :ne  38  :nw  37  :w  25  :sw  13  :se  14  } 
 27 { :e  28  :ne  39  :nw  38  :w  26  :sw  14  :se  15  } 
 28 { :e  29  :ne  40  :nw  39  :w  27  :sw  15  :se  16  } 
 29 { :e  30  :ne  41  :nw  40  :w  28  :sw  16  :se  17  } 
 30 { :e  31  :ne  42  :nw  41  :w  29  :sw  17  :se  18  } 
 31 { :e  32  :ne  43  :nw  42  :w  30  :sw  18  :se  19  } 
 32 { :e  33  :ne  44  :nw  43  :w  31  :sw  19  :se  20  } 
 33 { :e  34  :ne  45  :nw  44  :w  32  :sw  20  :se  21  } 
 34 { :e  35  :ne  46  :nw  45  :w  33  :sw  21  :se  22  } 
 35 { :nw  46  :w  34  :sw  22  :se  23  } 
36 { :e  37  :ne  47  :sw  24  :se  25  } 
37 { :e  38  :ne  48  :nw  47  :w  36  :sw  25  :se  26  } 
 38 { :e  39  :ne  49  :nw  48  :w  37  :sw  26  :se  27  } 
 39 { :e  40  :ne  50  :nw  49  :w  38  :sw  27  :se  28  } 
 40 { :e  41  :ne  51  :nw  50  :w  39  :sw  28  :se  29  } 
 41 { :e  42  :ne  52  :nw  51  :w  40  :sw  29  :se  30  } 
 42 { :e  43  :ne  53  :nw  52  :w  41  :sw  30  :se  31  } 
 43 { :e  44  :ne  54  :nw  53  :w  42  :sw  31  :se  32  } 
 44 { :e  45  :ne  55  :nw  54  :w  43  :sw  32  :se  33  } 
 45 { :e  46  :ne  56  :nw  55  :w  44  :sw  33  :se  34  } 
 46 { :nw  56  :w  45  :sw  34  :se  35  }

47 { :e  48  :ne  57 :sw  36  :se  37  } 
48 { :e  49  :ne  58  :nw  57  :w  47  :sw  37  :se  38  } 
 49 { :e  50  :ne  59  :nw  58  :w  48  :sw  38  :se  39  } 
 50 { :e  51  :ne  60  :nw  59  :w  49  :sw  39  :se  40  } 
 51 { :e  52  :ne  61  :nw  60  :w  50  :sw  40  :se  41  } 
 52 { :e  53  :ne  62  :nw  61  :w  51  :sw  41  :se  42  } 
 53 { :e  54  :ne  63  :nw  62  :w  52  :sw  42  :se  43  } 
 54 { :e  55  :ne  64  :nw  63  :w  53  :sw  43  :se  44  } 
 55 { :e  56  :ne  65  :nw  64  :w  54  :sw  44  :se  45  } 
 56 { :nw  65  :w  55  :sw  45  :se  46  } 
57 { :e  58  :ne  67  :nw  66  :sw  47  :se  48  } 
58 { :e  59  :ne  68  :nw  67  :w  57  :sw  48  :se  49  } 
 59 { :e  60  :ne  69  :nw  68  :w  58  :sw  49  :se  50  } 
 60 { :e  61  :ne  70  :nw  69  :w  59  :sw  50  :se  51  } 
 61 { :e  62  :ne  71  :nw  70  :w  60  :sw  51  :se  52  } 
 62 { :e  63  :ne  72  :nw  71  :w  61  :sw  52  :se  53  } 
 63 { :e  64  :ne  73  :nw  72  :w  62  :sw  53  :se  54  } 
 64 { :e  65  :ne  74  :nw  73  :w  63  :sw  54  :se  55  } 
 65 { :ne  75  :nw  74  :w  64  :sw  55  :se  56  } 
66 { :e  67  :ne  77  :nw  76  :se  57  } 
67 { :e  68  :ne  78  :nw  77  :w  66  :sw  57  :se  58  } 
 68 { :e  69  :ne  79  :nw  78  :w  67  :sw  58  :se  59  } 
 69 { :e  70  :ne  80  :nw  79  :w  68  :sw  59  :se  60  } 
 70 { :e  71  :ne  81  :nw  80  :w  69  :sw  60  :se  61  } 
 71 { :e  72  :ne  82  :nw  81  :w  70  :sw  61  :se  62  } 
 72 { :e  73  :ne  83  :nw  82  :w  71  :sw  62  :se  63  } 
 73 { :e  74  :ne  84  :nw  83  :w  72  :sw  63  :se  64  } 
 74 { :e  75  :ne  85  :nw  84  :w  73  :sw  64  :se  65  } 
 75 { :ne  86  :nw  85  :w  74  :sw  65 }
76 { :e  77  :ne  88  :nw  87  :se  66  } 
77 { :e  78  :ne  89  :nw  88  :w  76  :sw  66  :se  67  } 
 78 { :e  79  :ne  90  :nw  89  :w  77  :sw  67  :se  68  } 
 79 { :e  80  :ne  91  :nw  90  :w  78  :sw  68  :se  69  } 
 80 { :e  81  :ne  92  :nw  91  :w  79  :sw  69  :se  70  } 
 81 { :e  82  :ne  93  :nw  92  :w  80  :sw  70  :se  71  } 
 82 { :e  83  :ne  94  :nw  93  :w  81  :sw  71  :se  72  } 
 83 { :e  84  :ne  95  :nw  94  :w  82  :sw  72  :se  73  } 
 84 { :e  85  :ne  96  :nw  95  :w  83  :sw  73  :se  74  } 
 85 { :e  86  :ne  97  :nw  96  :w  84  :sw  74  :se  75  } 
 86 { :ne  98  :nw  97  :w  85  :sw  75 }
87 { :e  88  :ne  100  :nw  99  :se  76  } 
88 { :e  89  :ne  101  :nw  100  :w  87  :sw  76  :se  77  } 
 89 { :e  90  :ne  102  :nw  101  :w  88  :sw  77  :se  78  } 
 90 { :e  91  :ne  103  :nw  102  :w  89  :sw  78  :se  79  } 
 91 { :e  92  :ne  104  :nw  103  :w  90  :sw  79  :se  80  } 
 92 { :e  93  :ne  105  :nw  104  :w  91  :sw  80  :se  81  } 
 93 { :e  94  :ne  106  :nw  105  :w  92  :sw  81  :se  82  } 
 94 { :e  95  :ne  107  :nw  106  :w  93  :sw  82  :se  83  } 
 95 { :e  96  :ne  108  :nw  107  :w  94  :sw  83  :se  84  } 
 96 { :e  97  :ne  109  :nw  108  :w  95  :sw  84  :se  85  } 
 97 { :e  98  :ne  110  :nw  109  :w  96  :sw  85  :se  86  } 
 98 { :ne  111  :nw  110  :w  97  :sw  86 }
99 { :e  100  :se  87  } 
100 { :e  101  :w  99  :sw  87  :se  88  } 
 101 { :e  102  :w  100  :sw  88  :se  89  } 
 102 { :e  103  :w  101  :sw  89  :se  90  } 
 103 { :e  104  :w  102  :sw  90  :se  91 :ne 112 } 
 104 { :e  105  :w  103  :sw  91  :se  92  :ne 113 :nw 112 } 
 105 { :e  106  :w  104  :sw  92  :se  93  :ne 114 :nw 113} 
 106 { :e  107  :w  105  :sw  93  :se  94  :ne 115 :nw 114 } 
 107 { :e  108  :w  106  :sw  94  :se  95  :nw 115 } 
 108 { :e  109  :w  107  :sw  95  :se  96  } 
 109 { :e  110  :w  108  :sw  96  :se  97  } 
 110 { :e  111  :w  109  :sw  97  :se  98  } 
 111 { :w  110  :sw  98 }
112 { :e 113 :ne 116 :sw 103 :se 104 }
113 { :e 114 :ne 117 :nw 116 :w 112 :sw 104 :se 105 }
114 { :e 115 :ne 118 :nw 117 :w 113 :sw 105 :se 106 }
115 { :nw 118 :w 114 :sw 106 :se 107 }
116 { :e 115 :ne 119 :sw 112 :se 113 }
117 { :e 118 :ne 120 :nw 119 :w 116 :sw 113 :se 114 }
118 { :nw 120 :w 117 :sw 114 :se 115 }
119 { :e 120 :ne 121 :sw 116 :se 117 }
120 { :nw 121 :w 119 :sw 117 :se 118 }
121 { :sw 119 :se 120} 
}
)
