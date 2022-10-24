from flask import Flask
from flask import request
import base64
import os

# References
# https://flask.palletsprojects.com/en/2.2.x/
app = Flask(__name__)

@app.route('/', methods=['POST'])
def handle_request():
	imgTag = request.form['categoryName']
	imgName = request.form['imageName']
	imgBase64 = request.form['imageBase']
	base_to_Image(imgTag, imgName, imgBase64)
	return "Image Uploaded Successfuly"

def base_to_Image(imageTag, imageName, imageBase64):
	curDirectory = os.getcwd()
	categoryPath = os.path.join(curDirectory, imageTag)
	if not os.path.exists(categoryPath):
		os.makedirs(categoryPath)
	imgFile = open(os.path.join(categoryPath, imageName), 'wb')
	imgFile.write(base64.b64decode((imageBase64)))
	imgFile.close()

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, debug=True)