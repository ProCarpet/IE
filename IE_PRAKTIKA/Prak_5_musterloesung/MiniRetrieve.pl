use locale;

# MiniRetrieve.pl
# 2006/10/18 Martin Braschler
# (c) Zürcher Hochschule Winterthur

# usage: MiniRetrieve.pl <doc_directory> <query_directory>

$indir = shift;
$querydir = shift;

undef $/;

# read filename of documents

opendir INDIR, $indir or die;
@files = grep !/^\.\.?/, readdir INDIR;
closedir INDIR;

$numdocs = scalar @files;

# indexing of documents

print STDERR "Indexing....\n";

foreach $filename (@files) {
	open INFILE, "$indir/$filename" or die;
	$text = <INFILE>;
	close INFILE;
	
	foreach (split /\W+/, $text) {
		$_ = lc( $_ );

#invindex word->filename->freq
#noninvindex filename->word->freq

		$invindex{$_}{$filename} = 0 unless( defined( $invindex{$_}{$filename} ) );
		$invindex{$_}{$filename} += 1;
			
		$noninvindex{$filename}{$_} = 0 unless( defined( $noninvindex{$filename}{$_} ) );
		$noninvindex{$filename}{$_} += 1;
	}
}

# calculate all idfs and document normalizers

foreach $filename (keys %noninvindex) {
	$dnorm{$filename} = 0;
	
	foreach $word (keys %{ $noninvindex{$filename} } ) {
		unless( defined( $idf{$word} ) ) {
			@documents = keys %{ $invindex{$word} };
			$idf{$word} = log( ( 1 + $numdocs ) / ( 1 + scalar( @documents ) ) );
		}
		$a = $noninvindex{$filename}{$word} * $idf{$word};
		$dnorm{$filename} += ($a * $a);
	}
	
	$dnorm{$filename} = sqrt($dnorm{$filename});
}

# read queries

opendir QUERYDIR, $querydir or die;
@queryfiles = grep !/^\.\.?/, readdir QUERYDIR;
closedir QUERYDIR;

foreach $file (@queryfiles) {
	open INFILE, "$querydir/$file" or die;
	$querystring = <INFILE>;
	close INFILE;

	%{ $queries{$file} } = ();

	foreach $word (split /\W+/, $querystring) {
		next if( $word =~ /^\s*$/ );

		$queries{$file}{$word} = 0 unless( defined( $queries{$file}{$word} ) );
		$queries{$file}{$word} += 1;
	}
}

# process queries

foreach $queryid (sort by_queryid keys %queries) {
	print STDERR "Processing QUERY $queryid\n";

	$qnorm = 0;
	%accu = ();

	foreach $queryword (keys %{ $queries{$queryid} }) # process all query terms
		{
		unless( defined( $idf{$queryword} ) ) { # if query term does not occur in collection, we first have to calculate the idf
			$idf{$queryword} = log( 1 + $numdocs  );
		}
		$b = $queries{$queryid}{$queryword} * $idf{$queryword}; # qtf * idf
		$qnorm += ($b * $b);

		if( defined( $invindex{$queryword} ) ) # if query term occurs in collection
			{
			@documents = keys %{ $invindex{$queryword} };

			foreach $document (@documents) {
				$a = $invindex{$queryword}{$document} * $idf{$queryword};

# document scores are added up in accumulators. filename serves as document identifier

				$accu{$document} = 0 unless( defined( $accu{$document} ) );
				$accu{$document} += $a * $b;
			}
		}
	}
	$qnorm = sqrt( $qnorm );

# normalize length of vectors

	foreach (keys %accu) {
		if( $dnorm{$_} == 0 ) {
			$accu{$_} = 0;
			next;
		}
		$accu{$_} *= 1000;
		$accu{$_} /= ( $dnorm{$_} * $qnorm );
	}

# sort and return 1000 best results

	@results = sort by_rsv (keys %accu);
	@results = splice( @results, 0, 1000 );

	$rankcounter = 0;
	foreach( @results ) {
		print "$queryid Q0 $_ $rankcounter $accu{$_} miniRetrieve\n";
		$rankcounter += 1;
	}
}

sub by_rsv {
	$accu{$b} <=> $accu{$a};
}

sub by_queryid { # if filename of query contains number, sort numerically according to that number. Else, sort alphabetically
	if( $a =~ /(\d+)/ ) {
		$tmp = $1;
		if( $b =~ /(\d+)/ ) {
			return $tmp <=> $1;
		}
	}
	$a cmp $b;
}
