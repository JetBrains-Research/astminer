#!/bin/bash

if [[ $# -ne 2 || ! -f $1 ]] ; then
    echo "Invalid args / Check file "
    exit
fi

file_name=$1
output_name=$2

grep '^\s*#\s*include' $file_name > /tmp/include.c
grep -Pv '^\s*#\s*include\b' $file_name > /tmp/code.c
gcc -E /tmp/code.c | grep -v ^# > /tmp/preprocessed.c
mkdir -p $output_name
cat /tmp/include.c > $output_name/$file_name
cat /tmp/preprocessed.c >> $output_name/$file_name