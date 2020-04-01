# Zomato Sample
## Overview
**Zomato Sample** is a simple application which shows near restaurants and cafes (within the range of 10 kilometers) to users location. user can search nearby restaurants and also see more information by choosing each one.

## Technical Overview
The app is developed upon MVVM architecture. it has 2 data sources :
1. Location Provider to get user live location using **Play Services Location**
2. Remote data source based on this [API](https://developers.zomato.com/documentation#!/restaurant/search). we can fetch our required data by sending requests to __/search__ end point with user location latitude and longitude query parameters. server hasn't handled the state which no restaurant is found in requested location, so restuarants real distance to user location are double checked in the app.

## Design
Icons are from AndroidStudio built-in Material Icon pack. The illustration icons are from [iconfinder.com](https://iconfinder.com)
