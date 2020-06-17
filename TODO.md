# CommuteAPP TODO / Wishlist

## Minimum functionality before deployment
+ Implement proxy service for API calls - this removes the need to store API key within the APP
+ Implement scheduled notifications per user defined timings - this will require more deliberate testing, as notifications may only occur once every 8 hours.
+ Implement Intent action when user opens the app from a notification - currently only opens the app as normal. This should load a new fragment as the top of the main recycler view with the current alert/reminder details. This is also the only way (identified) to draw new polylines for scheduled routes (cannot run getMayAsyn from Worker).
+ Data validation for all user input - especially for data used in API calls.

## Wishlist
+ Improve route comparison logic. Add percentage of overlap. 
+ Improve notifications. Add buttons for 'Snooze' and 'Begin navigation'. Add expanded view. Find method to generate GoogleMap Bitmap in background.
+ Improve visual UI for consistency (mainly Colouring).
+ Normalise Database - currently all stored in a single table.