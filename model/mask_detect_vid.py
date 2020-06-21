#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Jun 11 11:41:57 2020

@author: harit
"""
import cv2
import numpy as np
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import img_to_array
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input
import time
import os
import pyrebase
from random import randint
Config = {
    'apiKey': "AIzaSyA-G6PYaFwtORq-fm3KG2M0tvLytH7QR80",
    'authDomain': "maskdetector-547a8.firebaseapp.com",
    'databaseURL': "https://maskdetector-547a8.firebaseio.com",
    'projectId': "maskdetector-547a8",
    'storageBucket': "maskdetector-547a8.appspot.com",
    'messagingSenderId': "996994080435",
    'appId': "1:996994080435:web:2402e1c0972bb629a61ac6"
  };


firebase = pyrebase.initialize_app(Config)
db = firebase.database()

def detect_and_predict_mask(frame, faceNet, model):
    # grab the dimensions of the frame and then construct a blob
    # from it
    (h, w) = frame.shape[:2]
    blob = cv2.dnn.blobFromImage(frame, 1.0, (300, 300),
        (104.0, 177.0, 123.0))

    # pass the blob through the network and obtain the face detections
    faceNet.setInput(blob)
    detections = faceNet.forward()
    

        ## initialize our list of faces, their corresponding locations,
    # and the list of predictions from our face mask network
    faces = []
    locs = []
    preds = []

    # loop over the detections
    for i in range(0, detections.shape[2]):
        # extract the confidence (i.e., probability) associated with
        # the detection
        confidence = detections[0, 0, i, 2]

        # filter out weak detections by ensuring the confidence is
        # greater than the minimum confidence
        if confidence > 0.5:
            # compute the (x, y)-coordinates of the bounding box for
            # the object
            box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
            (startX, startY, endX, endY) = box.astype("int")

            # ensure the bounding boxes fall within the dimensions of
            # the frame
            (startX, startY) = (max(0, startX), max(0, startY))
            (endX, endY) = (min(w - 1, endX), min(h - 1, endY))

            # extract the face ROI, convert it from BGR to RGB channel
            # ordering, resize it to 224x224, and preprocess it
            face = frame[startX:endX, startY:endY]
            face = cv2.cvtColor(face, cv2.COLOR_BGR2RGB)
            face = cv2.resize(face, (224, 224))
            face = img_to_array(face)/255.
            face = preprocess_input(face)
            face = np.expand_dims(face, axis=0)
            

            # add the face and bounding boxes to their respective
            # lists
            faces.append(face)
            locs.append((startX, startY, endX, endY))
        else:
            pass

    # only make a predictions if at least one face was detected
    if len(faces) > 0:
        # for faster inference we'll make batch predictions on *all*
        # faces at the same time rather than one-by-one predictions
        # in the above `for` loop
        preds = model.predict(faces)

    # return a 2-tuple of the face locations and their corresponding
    # locations
    return (locs, preds)

frame = [0,300,600,1200]


def DetectVideo(vidpath):
    count = 0
    
    # loop over the frames from the video stream
    while True:
        video_capture = cv2.VideoCapture(vidpath)
        #video_capture.set(cv2.CAP_PROP_POS_FRAMES, 30)
        time.sleep(2.0)
        success = True
        fps = int(video_capture.get(cv2.CAP_PROP_FPS))

        while success and count <= 1200:
            success,image = video_capture.read()
            print('read a new frame:',success)
            if count%(10*(fps)) == 0:
                cv2.imwrite('frames%d.jpg'%count,image)
                #frame = imutils.resize(frame, width=1000)
            count+=1
    # loop over the frames from the video stream
        for i in frame:
            image = cv2.imread('frames%d.jpg'%i)
            (locs, preds) = detect_and_predict_mask(image, faceNet, model)
            no_count = 0
            m_count = 0
            for (box, pred) in zip(locs, preds):
                (startX, startY, endX, endY) = box  # unpack the bounding box and predictions
                (mask,without_mask) = pred
                # determine the class label and color we'll use to draw
                #define bounding box and text
                if mask > without_mask:
                    label = 'Mask'
                    m_count+=1
                else:
                    label = "No Mask"
                    no_count+=1
                color = (0, 255, 0) if label == "Mask" else (0, 0, 255)
                label = "{}: {:.2f}%".format(label, max(mask, without_mask) * 100)
                # include the probability in the label
                cv2.putText(image, label, (startX, startY - 10),cv2.FONT_HERSHEY_SIMPLEX, 0.45, color, 2)
                cv2.rectangle(image, (startX, startY), (endX, endY), color, 2)
                print(label)
            # display the label and bounding box rectangle on the output
            # frame
            db.child('Cameras').child('Cam 1').push({'Latitude':'77.5011E','Longitude':'27.2038N','Place':'Mandi','no_mask': str(no_count),'mask':str(m_count)})
            cv2.imshow("Frame", image)
            key = cv2.waitKey(1) & 0xFF

        # if the `q` key was pressed, break from the loop
            if key == ord("q"):
                break

        # do a bit of cleanup
        video_capture.release()
        cv2.destroyAllWindows()
        return no_count, m_count

print("[INFO] loading face detector model...")
prototxtPath = os.path.join("C:/Users/abc/Desktop/covid19_face_mask_detection/face_detector/deploy.prototxt")
weightsPath = os.path.join("C:/Users/abc/Desktop/covid19_face_mask_detection/face_detector/res10_300x300_ssd_iter_140000.caffemodel")
faceNet = cv2.dnn.readNet(prototxtPath, weightsPath)

print("[INFO] loading face mask detector model...")
model = load_model("C:/Users/abc/Desktop/covid19_face_mask_detection/mask_model")
#print(model.summary())

print("[INFO] loading video stream...")
result = str(DetectVideo(0))





