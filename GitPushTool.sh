des=""
read -p "Please insert your des: " des
if [ -n "$des" ]
then
    git add -A
    git commit -a -m "$des"
    git push
else
    echo "@@@ Des can not null...."
fi
