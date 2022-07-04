import argparse

import dns
import dns.message
import dns.name
import dns.query
import time
import datetime

parser = argparse.ArgumentParser(description="mydig")
parser.add_argument('domain_name', type=str, help='The domain to resolve.')

root_servers = ["198.41.0.4", "199.9.14.201", "192.33.4.12", "199.7.91.13", "192.203.230.10",
                "192.5.5.241", "192.112.36.4", "198.97.190.53", "192.36.148.17", "192.58.128.30",
                "193.0.14.129", "199.7.83.42", "202.12.27.33"]


# resolves the domain from the given server list
def resolve_domain(domain_name, servers, dnstype):
    name = dns.name.from_text(domain_name)
    message = dns.message.make_query(name, dnstype)
    for server in servers:
        try:
            response = dns.query.udp(message, server, timeout=10)
            if response and response.rcode() == dns.rcode.NOERROR:
                return response
        except:
            # print("Failed to connect to a server.")
            pass
    return False


def resolve_ip(domain_name, s):
    # resolve ip from name server
    response = resolve_domain(domain_name, s, dns.rdatatype.A)
    if not response:
        return response

    # if we get cname
    if response.answer and response.answer[0].rdtype == dns.rdatatype.CNAME:
        cname = str(response.answer[0][0])
        return resolve_ip(cname, root_servers)

    # if we get an answer return the answer
    if response.answer:
        return response.answer

    # extract new domain/ip from response
    servers = get_ips(response.additional)

    # if additional empty query root with authority
    if not response.additional:
        name_server_found = False
        for rrset in response.authority:
            for domain in rrset:
                ips = resolve_ip(str(domain.target), root_servers)
                if ips:
                    servers = get_ips(ips)
                    name_server_found = True
                    break;
            if name_server_found:
                break

    # recurse
    return resolve_ip(domain_name, servers)


# rrset : additional field from response
def get_ips(rrsets):
    ips = []
    for rrset in rrsets:
        for ip in rrset:
            ips.append(ip.address)
    return ips

# output for the program
def display(domain_name, ttl, ip, time, date_of_request):
    print("-"*125)
    print("QUESTION SECTION:")
    print("{0:45s}IN A\n".format(str(domain_name)))
    print("ANSWER SECTION:")
    print("{0:40s}{1:5s}IN A              {2:30s}\n".format(str(domain_name), str(ttl), str(ip)))
    print("Query time: {0:.2f}ms".format(pow(10, 3)*time))
    print("WHEN:", date_of_request)


def main():
    args = parser.parse_args()
    start_time = time.time()
    result = resolve_ip(args.domain_name, root_servers)
    total_time = time.time() - start_time

    if not result:
        print("Failed to dig.")
        exit(0)
    display(args.domain_name, result[0].ttl, result[0][0].address, total_time, datetime.datetime.now())


if __name__ == '__main__':
    main()

