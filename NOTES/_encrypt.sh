#!/usr/bin/env bash

infile=$1
pass=$2
if openssl enc -e -aes256 -base64 -salt -pass "pass:${pass}" -in "${infile}" -out tmp.enc
then 
    mv ${infile} ~/.Trash/
    mv tmp.enc $1
elif [ -f tmp.enc ] ; then
    rm tmp.dec
fi
