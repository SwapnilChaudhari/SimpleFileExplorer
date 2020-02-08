**SimpleFileExplorer**

__Programing Language:__ 
Kotlin

__Tools:__
Android Studio

__Functionality:__
</br>Copy Files
</br>Paste Files
</br>Delete Files
</br>Move Files
</br>Create New Directory
</br>Multiple Select
</br>Rename Files
</br>Search 
</br>File Viewing

__Implementation concepts:__

*__1.Collections :__*
</br>One mutable list acts like a queue to retain directories from root directory to current open directory. While navigating through folders,
the last element of the list is always the current open directory.

One mutableSet of Files that contains absolute paths of all multiple selected files for copy, pste, move and delete operations.

*__2.Threading:__*
</br>All above mentioned operations are performed in a thread with runnable implementation.
</br>Handler is used to show progress of the all tasks on user interface

*__3.Interface:__*
</br>Custom interface OnDirectorySelected to provide cordination between different claases for click action on files and directory

*__5.Kotlin concepts:__*
</br>extended functions, Object class , range, lambda expressions

*__4.User Interface Components:__*
</br>RecyclerView,  CardView, Checkbox

__Logic:__
</br>The logic behind the development of the application is confined within only 3 collections: 
</br>1.mutable list to retain path to traverse through files and directory
</br>2.mutableSet to perform operations on selected file paths
</br>3.mutablList to retain immediate children files of the current directory  



*__Window__*
</br>
<img title ="one" src="https://github.com/SwapnilChaudhari/SimpleFileExplorer1/blob/master/Screenshots/Screenshot_20200208-144004_SmartFileExplorer.jpg" width="300" height="500" >

*__Search and View__*
</br>
<img src="https://github.com/SwapnilChaudhari/SimpleFileExplorer1/blob/master/Screenshots/Screenshot_20200208-175529_Android%20System.jpg" width="300" height="500" >

*__Items Long press__*
</br>
<img src="https://github.com/SwapnilChaudhari/SimpleFileExplorer1/blob/master/Screenshots/Screenshot_20200208-144016_SmartFileExplorer.jpg" width="300" height="500" >

*__Select Multiple__*
</br>
<img src="https://github.com/SwapnilChaudhari/SimpleFileExplorer1/blob/master/Screenshots/Screenshot_20200208-144026_SmartFileExplorer.jpg" width="300" height="500" >

*__Actions__*
</br>
<img src="https://github.com/SwapnilChaudhari/SimpleFileExplorer1/blob/master/Screenshots/Screenshot_20200208-144033_SmartFileExplorer.jpg" width="300" height="500" >

