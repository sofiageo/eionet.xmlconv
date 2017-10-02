#   Script for testing XMLCONV Conversion Service
#   The script is converting Excel file to XML.
#
#   Author: Enriko Kasper

import xmlrpclib
import requests

#server_url="http://converters.eionet.eu.int/RpcRouter"
server_url="http://converters.devel1.eionet.europa.eu/RpcRouter"
server = xmlrpclib.Server(server_url)

try:
    result1 = requests.get('http://converters.devel1.eionet.europa.eu/restapi/qascripts', headers={"Content-Type": 'application/json', 'Authorization': 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJlZWEiLCJpYXQiOjE0OTM4MDk5MzYsImV4cCI6MTUyNTM0NTkzNiwiYXVkIjoiY29udmVydGVycy1hcGkuZWlvbmV0LmV1cm9wYS5ldSIsInN1YiI6ImNkcmRldiJ9.mJIY4gzOkeXXSVxZMZqds__5VLFd27YjpMxZYm0Jukw'}, params={'schema' : 'http://rod.eionet.europa.eu/obligations/32'})
    print result1.json()
    result2 = requests.get('http://converters.devel1.eionet.europa.eu/restapi/qascripts', {"Content-Type": 'application/json', 'Authorization': 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJlZWEiLCJpYXQiOjE0OTM4MDk5MzYsImV4cCI6MTUyNTM0NTkzNiwiYXVkIjoiY29udmVydGVycy1hcGkuZWlvbmV0LmV1cm9wYS5ldSIsInN1YiI6ImNkcmRldiJ9.mJIY4gzOkeXXSVxZMZqds__5VLFd27YjpMxZYm0Jukw'})
    #excel file
    print result2.json()

    param1 = "https://cdrdev.eionet.europa.eu/ro/colwbzkmw/envwc4sa/CDDA_2018_RO_type2data_20170829.xls"

    method_result=server.ConversionService.convertDD_XML(param1)

except xmlrpclib.ProtocolError, p:
         err_code=p.errcode    #handle error according to error code

print method_result
