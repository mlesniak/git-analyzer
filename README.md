# Overview

The modification history of a program is represented by its version control log and can be a huge source of interesting
insights, in particular if combined with static source code analysis. This program analyses a git history and provides
multiple modules (currently: one) to compute fascinating insights.

## Installation

This is a standard gradle project, i.e. you build it via

```
gradle build
```

Note that in addition to the usual output in the `build/` directory a standalone executable file `git-analyzer` will be
created in the code's root directory as well (and removed on a `gradle clean`).

## Run

Start the analyzer without arguments to see the available options:

```
$ # gradle build
$ ./git-analyzer 
Value for option --directory should be always provided in command line.
Usage: git-analyzer options_list
Options: 
    --directory, -d -> Source code directory (always required) { String }
    --period, -p [128y] -> Period, e.g. 1y, 2w, 2w3d, ... { String }
    --analysis, -a [PackageExperts] -> Analysis to execute { Value should be one of [packageexperts] }
    --help, -h -> Usage info 
```

By default, you only need to provide a directory pointing to a git repository. For example, to gather statistics for
[JUnit5](https://github.com/junit-team/junit5) using the current default analyzer, write

```
$ git clone https://github.com/junit-team/junit5.git /tmp/junit5
$ ./git-analyzer --directory /tmp/junit5

{
<a lot of json output>
 "platform.tooling.support.tests" : {
    "Sam Brannen <sbrannen@vmware.com>" : 1,
    "Juliette de Rancourt <derancourt.juliette@gmail.com>" : 2,
    "Christian Stein <sormuras@gmail.com>" : 76,
    "JUnit Team <team@junit.org>" : 85,
    "Marc Philipp <mail@marcphilipp.de>" : 113
  },
  "standalone" : {
    "Christian Stein <sormuras@gmail.com>" : 1,
    "Sam Brannen <sbrannen@pivotal.io>" : 3,
    "JUnit Team <team@junit.org>" : 9
  }
}
```

The package `platform.tooling.support.tests` has been edited by five different accounts and Marc Philipp had the most
commits (113) and could be considered the technial and/or domain experts; note that this analysis might be skewed by "
team accounts" such as the "JUnit Team", so be aware of that.

By specifying `--period` you can restrict analysis to a specific period, e.g. last 100 days via `--period 100d`, last 3
months via `--period 3m` etc.

## Example: Interactive filtering with jq and fzf

TODO

## Modules

TODO

# TODO

- Complete documentation
- Write tests

# License

The source code is licensed under
the [Apache license](https://raw.githubusercontent.com/mlesniak/git-analyzer/main/LICENSE)
