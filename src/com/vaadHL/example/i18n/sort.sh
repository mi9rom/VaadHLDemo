for f in *.properties
do
 echo "Processing $f"
 sort $f > s.tmp
 cp s.tmp $f
done
rm s.tmp
cp Strings.properties Strings_en_US.properties

