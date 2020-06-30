wrk.method = "POST"
wrk.headers["Content-Type"] = "application/json"
wrk.headers["Authorization"]="Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiMyIsIui2hee6p-euoeeQhuWRmDIiXSwiZXhwIjoxNTkzNTE0OTc3LCJpYXQiOjE1OTM0OTY5Nzd9.E2Me1p2LOS2tdcpN-Q7EWYeZ2lH3-iAz41s1936A1JE"
wrk.body = '{"pagedParams":{"pageNum":1,"pageSize":10},"orderParams":{"orderPairs":[{"name":"userName","order":"ASCENDING"}]},"queryParams":{"userName":"","roleId":0}}'
