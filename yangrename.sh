#usage from workspace: yangLib/yangrename.sh <project directory> <project name> <project new name>
cd $1
ren=s/$2/$3/g;
sed -i -e $ren $2PC/.project
sed -i -e $ren $2Android/.project
sed -i -e $ren $2Android/AndroidManifest.xml
mv $2PC/ $3PC/
mv $2Android/ $3Android
cd ..
mv $1/ $3/