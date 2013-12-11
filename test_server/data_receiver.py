import sys
import BaseHTTPServer
from SimpleHTTPServer import SimpleHTTPRequestHandler
import urllib
import os
from datetime import datetime
import json
from jsonschema import validate	#git://github.com/Julian/jsonschema.git

schema = open('maiti_format_json.schema', 'r')

SCHEMA = schema.read()


class DataReceiverHandler(SimpleHTTPRequestHandler):
    
    def __init__(self,req,client_addr,server):
        SimpleHTTPRequestHandler.__init__(self,req,client_addr,server)
    
    def getDateStr(self):
        cur_time = datetime.now()
        return str(cur_time.year) + '-' + str(cur_time.month).zfill(2) + '-' + str(cur_time.day).zfill(2)
        
    def do_GET(self):
        DUMMY_RESPONSE = "OK"

        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.send_header("Content-length", len(DUMMY_RESPONSE))
        self.end_headers()
        self.wfile.write(DUMMY_RESPONSE)

    def do_POST(self):
        self.contentLength=int(self.headers.getheader("Content-Length")) 
        self.data = self.rfile.read(self.contentLength).strip()
	json_payload_start = self.data.find('payload=') + len('payload=');

        self.data = self.data[json_payload_start:]   # Get rid of the "input="  This is a hack, but this is only a test harness

	raw_json = self.data.replace("+", " ")  # Replace the + with spaces before we urldecode    
        raw_json = urllib.unquote(raw_json)

        print "Raw: " + raw_json
        fjson = json.loads(raw_json)
        #print fjson
        


        filename = "mobile_records_" + self.getDateStr() + ".csv"
        
        fields = [
		  'cust_id', 'app_id', 'type', 'name', 'dur', 'ver', 'model', 'agent_type', 'os', 'pkg_id',
		  'mem_free', 'mem_total', 'code_ver', 'conn', 'ser_num', 'id', 'parent_id', 'sess_id', 
		  'offset_ms', 'error', 'utag1', 'utag2', 'utag3', 'udata'
		 ]
        
        if os.path.exists(filename):
            f=open(filename, 'a')

        else:
            f=open(filename, 'w')
            header_string = "date,"
            for field in fields:
                header_string = header_string + field + ","

            f.write(header_string + "\n")

        timeStamp=datetime.now()

        for line in fjson:
            outputLine = "%s," % (timeStamp.strftime("%d/%m/%Y %H:%M"))
            for field in fields:
                if field in line:
                    outputLine = outputLine + str(line[field]).replace(',', ';')
                outputLine = outputLine + ","
            
            f.write(outputLine + "\n")

            #print "Line: " + outputLine
    	    if line['type'] == "interval":
	        print "Interval Transaction: " + line['name'] + "\t\tDuration: " + str( line['dur'] );
	    else:
	        print "Notification Transaction: " + line['name']# + "\t\tUserTag1: " + line['utag1'];

        
	
        validate(fjson, json.loads(SCHEMA))

        self.do_GET()


HandlerClass = DataReceiverHandler
ServerClass  = BaseHTTPServer.HTTPServer
Protocol     = "HTTP/1.0"

if sys.argv[1:]:
    port = int(sys.argv[1])
else:
    port = 8001
server_address = ('0.0.0.0', port)

HandlerClass.protocol_version = Protocol
httpd = ServerClass(server_address, HandlerClass)

sa = httpd.socket.getsockname()
print "Serving HTTP on", sa[0], "port", sa[1], "..."
httpd.serve_forever()


