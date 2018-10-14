#!/usr/bin/env bash

infile=$1
outfile=$2
pass=$3
openssl enc -e -aes256 -base64 -salt -pass "pass:${pass}" -in "${infile}" -out "${outfile}"
