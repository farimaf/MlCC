import pandas as pd
import numpy as np
import time as time
from sklearn.metrics import classification_report
from sklearn.metrics import confusion_matrix
import pickle
import socket

start_time = time.time()
print("imports complete")
modelfilename = 'cart_model.sav'
# path_test="./test/test.txt"
colNames=["block1", "block2", "isClone", "COMP", "NOCL", "NOS", "HLTH", "HVOC", "HEFF", "HBUG", "CREF", "XMET", "LMET", "NLOC", "NOC", "NOA", "MOD", "HDIF", "VDEC", "EXCT", "EXCR", "CAST", "TDN", "HVOL", "NAND", "VREF", "NOPR", "MDN", "NEXP", "LOOP"]

#load model
loaded_model = pickle.load(open(modelfilename, 'rb'))
print("model loaded")
# data='com.liferay.portal.lar.PermissionImporter.importPermissions_6,com.liferay.portlet.wiki.action.ExportPageAction.getFile,0,33.33,0.0,22.22,21.6,13.59,9.89,24.0,9.52,8.7,100.0,17.24,0.0,11.11,0.0,31.58,0.0,0.0,0.0,0.0,42.86,24.07,22.22,27.42,20.89,33.33,36.36,100.0'
# data=data.replace(',','~~')

tp=0
fn=0
num_clones=0

def process(data):
    global num_clones, tp, fn
    if "FINISHED_JOB" in data:
        print
        np.array(list(data))
        print("EXITING NOW")
        return 0
    array = np.array(data.split('~~'))
    label = bool(int(array[2]))
    X_test = array[[i for i in range(0, 30) if i not in [0, 1, 2, 4, 14]]]
    X_test = [float(x) for x in X_test]
    Y_test = array[2]
    methodpair = array[[i for i in range(0, 2)]]
    X_test = np.reshape(X_test, (1, len(X_test)))

    prediction = loaded_model.predict(X_test)

    if label:
        num_clones = num_clones + 1
        if label == prediction:
            tp = tp + 1
        elif label != prediction:
            fn = fn + 1
    return 1


serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversocket.bind(('localhost', 9999))
serversocket.listen(1) # maximum  1 connection
connection, address = serversocket.accept()
print("Connection accepted")
data=""
while True:
    chunk = connection.recv(2)
    chunk = chunk.decode('utf-8')
    if chunk and len(chunk) > 0:
        data = "{d}{c}".format(d=data,c=chunk)
        if "\n" in data:
            lines = data.split("\n")
            for index in range(0,len(lines)-1):
                if process(lines[index])==0:
                    break
            data = lines[index+1]
    else:
        break

end_time = time.time()
print("whole process took: "+(end_time-start_time))
print("finished at: "+end_time)
recall=tp/num_clones
print('recall: '+recall);