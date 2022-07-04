# import matplotlib
# import os
# import pandas
# import dns.resolver
# import time
#
# resolver = dns.resolver.Resolver()
#
top_sites = ["chaturbate.com", "microsoft.com", "netflix.com",
             "jd.com", "instagram.com", "zoom.us", "taobao.com", "qq.com", "baidu.com", "google.com"]
# mydig_times = []
#
# for site in top_sites:
#     times = []
#     times.append(site)
#     for x in range(0, 10):
#         # experiment 1
#         # times.append(float(os.popen("python mydig.py " + site).read()))
#         start_time = time.time()
#         dns.resolver.resolve(site)
#         total_time = time.time() - start_time
#         times.append(total_time)
#
#     mydig_times.append(times)
#
# mydig_df = pandas.DataFrame(mydig_times, columns=['website', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10'])
# print(mydig_df)
# mydig_df.to_csv("google_dns_times.csv")
#
import socket
import time
import pandas as pd
import dns.resolver


# top_websites = [
#     "www.google.com", "www.youtube.com", "www.facebook.com",
#     "www.amazon.com", "www.yahoo.com", "www.netflix.com",
#     "www.microsoft.com", "www.reddit.com", "www.instagram.com",
#     "www.baidu.com"
# ]

resolver = dns.resolver.Resolver()
hostname = socket.gethostbyaddr("8.8.4.4")
resolver.nameservers=[socket.gethostbyname('dns.google')]

myDig_times=[]
for website in top_sites:
    times = []
    times.append(website)
    for x in range(0, 10):
        start = time.time()
        print(resolver.resolve(website))
        stop = time.time() - start
        times.append(stop)
    myDig_times.append(times)

myDig_df = pd.DataFrame(myDig_times, columns=['website', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10'])
print(myDig_df)
myDig_df.to_csv("google_DNS_times.csv")
