wrk -t5 -c10 -d10s --script=testGetUserInfoPerformance.lua --latency http://localhost:10000/demo/getUserInfo
