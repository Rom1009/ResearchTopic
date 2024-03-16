RESEARCH TOPIC: PROBABILISTIC WEIGHTED
FREQUENT ITEMSET MINING OVER UNCERTAIN DATA STREAMS

Environment: 
openjdk 11.0.20 2023-07-18
OpenJDK Runtime Environment Temurin-11.0.20+8 (build 11.0.20+8)
OpenJDK 64-Bit Server VM Temurin-11.0.20+8 (build 11.0.20+8, mixed mode)

How to run code: 
Open file code, go through Test.java. We have folder data including 3 uncertain database as well as mean and variance in each. After run, you must change file name in Test file like this 

UncertainDatabase database = new UncertainDatabase("data/connect_0.78_0.65.txt", 0.78, Math.sqrt(0.65));

I also have define batch to change how many batch you want to run. 
Finally, you write javac Test.java to tun. 

