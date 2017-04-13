# Cookview

Cookview presents you with lots of delicious food images and comments users
have made.

## Resources used
* imgur api	// source of images and comments
* glide		// for image loading and caching
* volley	// for api interactions

## Building
You'll need to add your own API keys in the `buildConfigField`s inside
`build.gradle` or else you will get authorization errors from the imgur API.

## Notes
This project was created without much intention other than playing with
glide and volley and the imgur api. Please feel free to make PRs to improve it
if you wish.
