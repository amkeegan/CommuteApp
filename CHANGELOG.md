# CommuteApp Changelog

17 June 2020
+ Added application notifications (static, not indicative of app state)
+ Added framework for route comparison: loads stored route, makes Directions API call for new, current route. Currently simply inspects first StartAddress for comparison.

15 June 2020
+ Routes displayed on GoogleMap prioritise reading from DB
+ DirectionAPI results are serialised and stored in Room DB

## **Week 6 (Deakin Study Week)**
08 June 2020
+ GoogleMap and DirectionsApi implemented. Directions are generated from Data in Room Database, and corresponding PolyLine is added to Map on screen.

06 June 2020
+ GoogleMap implmented in recycler view, currently only loads default map, with not markers.


## **Week 5 (Deakin Week 12 / Study Week)**
31 May - 05 Jun 2020
### **Updated 05 June 2020**
+ **05 June 2020**
    - Added Display Layouts, can be accessed from Main view by touching a card.
    - Display fragment allows for OnBackPressed
    - Reworked Layout XML files to include all UI elements for Display fragment in one file
+ **04 June 2020**
    - Implemented SQLite storage for persistence - uses ROOM ORM
    - Implemented Main Recycler View, cards generated from entries in DB
+ **03 June 2020**
    - Reworked some existing Layout XML files
## **Week 4 (Deakin Week 11)**
24-30 May 2020
## **Week 3 (Deakin Week 10)**
17-23 May 2020
## **Week 2 (Deakin Week 9)**
10-16 May 2020

### **Updated 10 May 2020**  
+ New Android Layout XML files created for each component
    - Main View - Recycler View
    - Main View - Recycler View item
    - Commute View - Recycler View
    - Commute View - Recycler items (text input, schedule buttons, reminder buttons, confirmation buttons)
+ Very basic design work applied to layout files
    - Circular shape applied to Schedule button
    - Rounded rectangle shape applied to Reminder Button
+ No programming completed, no test rendering of layouts.

## **Week 1 (Deakin Week 8)**
03-09 May 2020
### **Updated 08 May 2020**

+ **08 May 2020**  
    - Github repository initialised.  
    - Minimum file/folder structure created.  
    - Previous created assets moved to repository.  
    - Android Studio project initialised and stored in src/

+ **04 May 2020 (Back dated)**  
    - App sketch turned into semi-functional prototype in Adobe XD.  
    - Prototype stored in Design/

+ **03 May 2020 (Back dated)**  
    - Concept App sketches completed with OneNote.  
    - Sketch stored in Design/