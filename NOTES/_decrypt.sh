#!/usr/bin/env bash

infile=$1
pass=$2
if openssl enc -d -aes256 -base64 -salt -pass "pass:${pass}" -in "${infile}" -out tmp.dec
then 
    mv ${infile} ~/.Trash/
    mv tmp.dec $1
elif [ -f tmp.dec ] ; then
    rm tmp.dec
fi
