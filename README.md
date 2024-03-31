# GUIDE

## Convention when creating merge request

### how to name a branch?
bug fix --> bugfix/FEM-<ID>\
feature --> feat/FEM-<ID>\
others --> chore/FEM-<ID>\

e.g.\
feat/FEM-25, bugfix/FEM-26

### how to set title of merge request?
feat: [FEM-25]: Your content here\
bugfix: [FEM-26]: Your content here

## App Distibution
### How to get latest app for testing purposes?
Please click on the [run workflow](https://github.com/CharmFlex-98/FlexiExpensesManager_Android/actions/workflows/firebase-distribute.yml) located at the top right corner. This will generate a new build of the app and once it is done, you should able to receive an email.\
You can also navigate to [firebase console](https://console.firebase.google.com/u/1/project/flexiexpensesmanagerproject/appdistribution/app/android:com.charmflex.flexiexpensesmanager/releases) to get the latest build if you are one of the collaborators.
