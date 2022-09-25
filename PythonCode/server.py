import flask
from PIL import Image
import werkzeug
import base64
from io import BytesIO
import requests
from datetime import datetime
import time
import os

app = flask.Flask(__name__)

@app.route("/upload_image", methods=["POST"])
def uploadImage():
	files_ids = list(flask.request.files)
	category = flask.request.form['requestBody']
	curDir = os.getcwd()
	image_num = 1
	for file_id in files_ids:
		imagefile = flask.request.files[file_id]
		fileName = werkzeug.utils.secure_filename(imagefile.filename)
		currentTimeStr = time.strftime("%Y%m%d-%H%M%S")
		newDir = os.path.join(curDir, category)
		if not os.path.exists(newDir):
			os.makedirs(newDir)
		imagefile.save(newDir+'/' + currentTimeStr+'_'+fileName)
		image_num = image_num + 1
		print("\n")
	return {
		"message": "Image Uploaded Successfully",
		"status": "Success"
	}



if __name__ == "__main__":
	app.run(host='0.0.0.0', port="8000", debug=True)