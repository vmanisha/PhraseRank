#!/usr/bin/perl -W
open(IN,$ARGV[0]);

my @array = ();
while(<IN>) {
	chomp;
	if ($_ =~ /^(\S+)\s+(\S+)\s+(\S+)$/) {
		push(@array,$2." ".$1." ".$3);	
	}
}
close(IN);

@keysQNo = sort @array;

$currentKey = 0;
foreach $line (@keysQNo) {
	if ($line =~ /^(\S+)/) {
		$key = $1;
		if ($key != $currentKey) {
			if ($currentKey != 0) {
				print "\n\n".$line."\n";
			} else {
				print $line."\n";	
			}
			$currentKey = $key;
		} else {
		print $line."\n";
		}	
	}
}
