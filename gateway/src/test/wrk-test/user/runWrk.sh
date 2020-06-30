wrk -t5 -c5 -d10s --script=testUserList.lua --latency http://gateway.k3s.com/user-service/user/table
