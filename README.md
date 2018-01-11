# Thingo
The code for for thingo.
Any new platforms should be added to sub folders.

## Folder Structure
- android: the android version
- categories: holds TSV files which hold all the category and board information
- category-server: a RESTful server that serves category information to the apps
- pattern-creator: holds the pattern.tsv file, which the app versions use for pattern matching, and a Java swing app to edit it

Check each project for its own README file.

## New Category Workflow
- Use the [Category Template](https://docs.google.com/spreadsheets/d/1u62Q9ueQddll7_lgGMFd6FxEpJd30frjH8mAV3CdY_4/edit#gid=0) to start a new category.
- Replace any text surrounded by < > 's.
- Do not remove the first row, it's needed by the applications.
- You need to have at least 27 rows in this spreadsheet to make a valid category (24 rows for the phrases to fill a board, 1 for the free space, and 1 for the top row (phrase,description)).
- The name of the spreadsheet is the name of the category.
- When finished, File -> Download As ... -> Tab-separated values.
- Save the file into the phrases folder. The applications should pick it up automatically.
