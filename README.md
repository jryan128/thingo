bazingo
=======
The main repository for Bazingo.
Any new platforms should be add to sub folders.

Folder Structure
----------------
 - phrases: holds TSV files which hold all the category and board information
 - web: the jquery mobile and node.js version of Bazingo

Workflow
--------
 - Use the [Category Template](https://docs.google.com/spreadsheets/d/1u62Q9ueQddll7_lgGMFd6FxEpJd30frjH8mAV3CdY_4/edit#gid=0) to start a new category.
 - Replace any text surrounded by < > 's.
 - Do not remove the first row, it's need by the applications.
 - You need to have at least 27 rows in this spreadsheet to make a valid category (24 rows for the phrases to fill a board, 1 for the free space, and 1 for the top row (phrase,description)).
 - The name of the spreadsheet is the name of the category.
 - When finished, File -> Download As ... -> Tab-separated values.
 - Save the file into the phrases folder. The applications should pick it up automatically.
