Steps to getting Git to work:

1) Create Git account
2) Tell John the username, so I can add you to the organization
3) Go to http://help.github.com/win-set-up-git/ (if you're on a windows machine) to download and set up Git on your machine
4) Follow instructions there
5) Once that is setup, time to get connected:
git clone git@github.com:TeamCourageWolf/Class-Project.git
cd Class-Project
git remote add upstream git://github.com/TeamCourageWolf/Class-Project.git
git fetch upstream
6) Okay, you should have the repository on your machine now.
7) Make a change to README.txt, just by opening it in a text editor and adding in something like "Your name was here"
8) then do the following:
git add README.txt
git status
git commit -m 'your name commit'
git push origin master




Testing on a different computer

Sweet, I can push/pull files using Git. Now to add everyone else from the team.

-John

VAMSI WOZ HERE!!! MUAHAHAHA!!!!