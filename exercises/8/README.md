- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  * [Description](#description)
  * [Part 1: CI/CD Pipelines](#part-1-cicd-pipelines)
    + [Create Classroom50 Repository](#create-classroom50-repository)
    + [Add Hello World Workflow](#add-hello-world-workflow)
    + [Add Maven CI Workflow](#add-maven-ci-workflow)
    + [View results of Maven CI Workflow maven_test job](#view-results-of-maven-ci-workflow-maven_test-job)
    + [Debug Maven CI Workflow update_dependence_graph job](#debug-maven-ci-workflow-update_dependence_graph-job)
      - [GitHub Caches](#github-caches)
      - [Job Dependencies](#job-dependencies)
      - [Job Permissions](#job-permissions)
    + [Enable Dependabot](#enable-dependabot)
    + [Add CodeQL TaaS Workflow](#add-codeql-taas-workflow)
    + [Add Maven Publish Workflow](#add-maven-publish-workflow)
    + [Deploy Maven package and use in your Maven project](#deploy-maven-package-and-use-in-your-maven-project)
    + [Submission](#submission)
  * [Part 2: Dockers](#part-2-dockers)
    + [Create Classroom50 Repository](#create-classroom50-repository-1)
    + [Prerequisites](#prerequisites)
    + [Do some sanity tests](#do-some-sanity-tests)
    + [Create Docker image](#create-docker-image)
    + [Create Docker Container](#create-docker-container)
    + [Import Playwright Tests](#import-playwright-tests)
    + [Add CI Test Workflow](#add-ci-test-workflow)
    + [Add Docker Publish Workflow](#add-docker-publish-workflow)
    + [Pull published Docker image and launch from desktop](#pull-published-docker-image-and-launch-from-desktop)
    + [(Optional) Run Maven CI inside a Container](#optional-run-maven-ci-inside-a-container)
    + [Submission](#submission-1)

# CS 1632 - Software Quality Assurance
Fall Semester 2026 - Exercise 8 

* DUE: November 11 (Wednesday), 2026 before start of class

## Description

During the semester, we learned various ways in which we can automate testing.
But all that automation is of no use if your software organization as a whole
does not invoke those automated test scripts diligently.  Preferably, those test
scripts should be run before every single source code change to the repository,
and for good measure, regularly every night or every weekend just in case.  Now,
there are many reasons why this does not happen if left to individual
developers:

1. Developers are human beings so they forget.  Or, they remember to run
   some tests, but not all the test suites that are relevant to the changes
they have made.

1. Developers are sometimes on a tight schedule, so they are tempted to skip
   testing that may delay them, especially if they are not automated.  They
justify their actions by telling themselves that they will fix the failing
tests "as soon as possible", or that the test cases are not testing anything
important, or that failing test cases in modules under the purview of
another team "is not my problem".

In Part 1 of this exercise, we will learn how to build an automated
"pipeline" of events that get triggered automatically under certain
conditions (e.g. a source code push).  A pipeline can automate the entire
process from source code push to software delivery to end users, making sure
that a suite of tests are invoked as part of the process before software is
delivered.  Pipelines that are built for this purpose are called CI/CD
(Continuous Integration / Continuous Delivery) pipelines, because they
enable continuous delivery of software to the end user at high velocity
while still maintaining high quality.  We will learn how to build a fully
functioning pipeline for the [Rent-A-Cat application](../2) that
we tested for Exercise 2 on our GitHub repository.

In Part 2, we will learn how to use Docker to both test and deploy software as
part of a CI/CD pipeline.  Docker containers are lightweight, isolated
execution environments which can reproduce the execution environments in the
deployment sites (OS userland, libraries, webservers, databases, etc.) so that
software can be tested in situ.  In our case, we will create a docker image
out of the [Rent-A-Cat website](https://cs1632.appspot.com/) that we tested
for Deliverable 3 for testing and deployment.

## Part 1: CI/CD Pipelines

In Part 1, you will learn how to create a pipeline from scratch based on the
Rent-A-Cat application for Exercise 2, using the CI/CD support provided by
your GitHub repository through GitHub Actions.  GitHub Actions is just one
example CI/CD framework.  Other widely used CI/CD frameworks include GitLab
CI/CD and Jenkins.  Regardless of which you choose, they work in mostly
similar ways: there is a configuration file that describes actions in the
pipeline (written in YAML for GitHub Actions and GitLab CI/CD, and in a
Groovy-based language in a Jenkinsfile for Jenkins) and the actions are
performed on one or more Runner machines, which are virtual machines or Docker
containers depending on how the framework is configured (GitHub-hosted Runners
are virtual machines that are newly created for each job and discarded
afterwards).  The main thing that differs is the configuration file syntax.
Hence, by learning GitHub Actions, you will be able to translate that
knowledge to other frameworks as well.  In GitHub Actions lingo, pipelines are
called **workflows**, and the two terms will be used interchangeably.

### Create Classroom50 Repository

Please use the link for Part 1 posted on the Teams Exercise 8 channel to
accept this exercise and create your repository.

### Add Hello World Workflow

Let's first start with a very basic workflow which prints "Hello World"
inside the Runner.  Click on the "Actions" menu on your GitHub repository
webpage (it is near the top).  Then click on the "New workflow" button on
the left hand side.  You will be presented with a plethora of "starter"
workflows for different purposes.  Search for "manual" in the search box and
you should see a single workflow named "Manual workflow" by GitHub Actions
in the search results.  Click on the "Configure" button on the workflow.
Then click on the "Commit changes..." button once you are done reviewing the
workflow and then commit the file.  Note that this creates a workflow YAML
file ".github/workflows/manual.yml" under your repository.

Please refer to the following tutorial to see exactly where to click:
https://docs.github.com/en/actions/using-workflows/using-starter-workflows

Now let's take a close look at the manual.yml YAML file.  Below are the
file contents:

```
# This is a basic workflow that is manually triggered

name: Manual workflow

# Controls when the action will run. Workflow runs when manually triggered using the UI or API.
on:
  workflow_dispatch:
    # Inputs the workflow accepts.
    inputs:
      name:
        # Friendly description to be shown in the UI instead of 'name'
        description: 'Person to greet'
        # Default value if no value is explicitly provided
        default: 'World'
        # Input has to be provided for the workflow to run
        required: true

# A workflow run is made up of one or more jobs that can run sequentially or in parallel
jobs:
  # This workflow contains a single job called "greet"
  greet:
    # The type of runner that the job will run on
    runs-on: ubuntu-latest

    # Steps represent a sequence of tasks that will be executed as part of the job
    steps:
    # Runs a single command using the runners shell
    - name: Send greeting
      run: echo "Hello ${{ github.event.inputs.name }}"
```

Please read the comments starting with # carefully.  In essence, a workflow
is composed of one or more jobs.  Jobs are run on individual Runners (in
parallel by default).  A job is composed of one or more sequential steps
which are performed on the same Runner.  The "Manual workflow" is composed
of a single job named "greet" which is in turn composed of a single step
"Send greeting".

Now let's put this workflow into action by executing it on a Runner!  Click
on the "Actions" menu again and you should see a new workflow named "Manual
workflow" on your lefthand side.  Click on it.  Then click on the "Run
workflow" button.  You will get a pop up with an option to change "Person to
greet".  Leave everything as-is and click on the green "Run workflow" button
again.  After a couple of seconds, you will see a new "Manual workflow" run
appear on the list of runs with an orange dot and the orange dot will soon
turn into a green checkmark.  The orange dot indicates that the workflow is
under execution and the green checkmark indicates that it completed
successfully.  A failure is indicated by a red crossmark (which we will
encounter later).  Now click on the "Manual workflow" run link to see the
details of the run.  You should see a screen that looks like this:

<img alt="Manual workflow run" src=img/manual_workflow_1.png>

The screen shows an overview of the workflow.  The workflow is composed of a
single "greet" job as configured in the YAML file.  The green checkmark
beside the job indicates success.  Now click on the "greet" job to peek into
the job and you should see the below screen:

<img alt="Manual workflow greet job" src=img/manual_workflow_2.png>

As you see, the job is composed of 3 steps: "Set up job", "Send greeting", and
"Complete job".  The steps "Set up job" and "Complete job" are implicitly
inserted into every job even though they are not specified in the YAML file.
I've expanded the first two steps for viewing.  The purpose of "Set up job" is
to set up the Runner virtual machine within which the job is to run.  You can
see that the Runner was created using the ubuntu-20.04 runner image, and that
is because "runs-on: ubuntu-latest" was specified on the YAML file and 20.04
happened to be the latest version when this screenshot was taken
(ubuntu-latest has since moved on to newer Ubuntu LTS releases).  The "Send
greeting" step runs the commandline specified in the "run:" entry in the YAML
file.

The reason that we were able to trigger this workflow manually was because
of the following lines on the YAML file:

```
on:
  workflow_dispatch:
```

Typically, workflows are triggered automatically in response to some
repository event but it is useful to be able to sometimes trigger them
manually.

Again, refer to the below tutorial if you are confused on where to click:
https://docs.github.com/en/actions/quickstart

### Add Maven CI Workflow

Now let's get down to business and try writing a CI workflow for our Maven
project.  This time we are going to have two jobs: 1) A "maven_test" job for
invoking "mvn test" on our project and 2) An "update_dependence_graph" job
for updating the package dependence graph in your GitHub repository.  The
dependence graph is used by a GitHub bot called Dependabot to notify the
repository owner of packages used by the repository that have become stale
or have outstanding security vulnerabilities.

Click on the "Actions" menu again and then click on the "New workflow"
button.  Now, instead of choosing a pre-existing "starter" workflow like
before, click on the "set up a workflow yourself" link.  This will take you
to a page for editing ".github/workflows/main.yml" from a blank slate.
Please change the file name to "maven-ci.yml" instead and then paste the
following into the content box before committing the file:

```
name: Maven CI

# Triggers manually or on push or pull request on the main branch
# (in other words, on a code integration event.)
on:
  workflow_dispatch:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  
  # Runs the Maven test phase on the project
  maven_test:

    runs-on: ubuntu-latest

    permissions:
      contents: read

    steps:

    # The uses: keyword invokes a GitHub action:
    # https://github.com/marketplace/actions/checkout
    - name: Checkout repository
      uses: actions/checkout@v6

    # This invokes the Setup Java JDK GitHub action:
    # https://github.com/marketplace/actions/setup-java-jdk
    - name: Set up JDK 11
      uses: actions/setup-java@v5
      with:
        java-version: '11'
        distribution: 'zulu'
        cache: maven

    - name: Test with Maven
      run: mvn test

    # https://github.com/marketplace/actions/upload-a-build-artifact
    - name: Upload jacoco results as artifact
      uses: actions/upload-artifact@v6
      with:
        name: Jacoco coverage results
        path: target/site/jacoco

  # Uploads dependency graph to GitHub to receive Dependabot alerts 
  update_dependence_graph:

    runs-on: ubuntu-latest

    permissions:
      contents: read

    steps:

    # https://github.com/marketplace/actions/maven-dependency-tree-dependency-submission
    - name: Update dependency graph
      uses: advanced-security/maven-dependency-submission-action@v5

```

Alternatively, you could have just created and committed the file
".github/workflows/maven-ci.yml" and it would have had the same effect.  Now
go ahead and click on "Actions" and you will see the Maven CI workflow
created and already running!  Soon, it will show a failure:

<img alt="Maven CI workflow failure" src=img/maven_ci_workflow_1.png>

We will get to the failure soon, but first let's try to understand why the
workflow was triggered without you manually running it.  Note the phrase
"Maven CI #1: Commit 8e12112 pushed by wonsunahn" below the commit message
"Create maven-ci.yml".  It is clear that this workflow was triggered due to
a pushed commit.  These lines at the top of maven-ci.yml file are what
automatically triggered the workflow on a push:

```
# Triggers manually or on push or pull request on the main branch
# (in other words, on a code integration event.)
on:
  workflow_dispatch:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]
```

CI pipelines are those that run in response to a code integration event.
Going forward, any push or creation of a pull request will trigger this
workflow.  A pull request is a request to merge a branch into the main trunk
of the repository and is in that sense also a code integration event.

### View results of Maven CI Workflow maven_test job

Now let's click on the failed run link and see exactly which job(s) failed:

<img alt="Maven CI workflow first run" src=img/maven_ci_workflow_6.png>

You can see that the "maven_test" job passed, but the "update_dependence_graph"
job failed.  We will get to that job soon, but we should be able to view the
results of the maven_test job since it passed.  So what are the results?  By
default jobs do not publish any results beyond the green check mark that
indicates that the job passed.  So what does it mean for a job to have passed?
It means that every step in the job completed successfully including the "mvn
test" step.  If "mvn test" suffered a failure, it would have returned a
non-zero [exit code](https://en.wikipedia.org/wiki/Exit_status) (like all Linux
processes do), which is detected by the workflow.  So you can be confident that
all your JUnit tests passed.

If a job wants to publish results beyond the green check mark, then it needs to
create what is called an **artifact**.  You can see in the above maven-ci.yml
file that the job has a step to create an artifact out of the
"target/site/jacoco" path that contains Jacoco code coverage analysis results.
The artifact is available at the bottom of the workflow Summary page you are on
if you scroll down:

<img alt="Maven CI workflow artifact" src=img/maven_ci_artifact.png>

Click on the "Jacoco coverage results" link and that would download a zip file
that contains the artifact.  See if you can decompress it and view the code
coverage results.  Don't be surprised if you see zero coverage because this is
just the scaffolding code from Exercise 2.  If you replace it with real tests,
you will see actual coverage.

### Debug Maven CI Workflow update_dependence_graph job

Now time to look at the still failing "update_dependence_graph" job:

<img alt="update_dependence_graph job failure: no POM" src=img/maven_ci_workflow_7.png>

Note the phrase "Goal requires a project to execute but there is no POM in
this directory".  The depgraph Maven plugin needs the pom.xml file to
extract the dependence graph but it looks like there is none under the
current directory.  Why would this be?  Let's take a closer look at the job
definition on our maven-ci.yml file once more:

```
  # Uploads dependency graph to GitHub to receive Dependabot alerts 
  update_dependence_graph:

    runs-on: ubuntu-latest

    permissions:
      contents: read

    steps:

    # https://github.com/marketplace/actions/maven-dependency-tree-dependency-submission
    - name: Update dependency graph
      uses: advanced-security/maven-dependency-submission-action@v5
```

Note that there is nowhere that we bring in the pom.xml file!  But how about
the previous checkout action?  

```
    - name: Checkout repository
      uses: actions/checkout@v6
```

Besides, weren't we able to successfully run "mvn test" because there was a
pom.xml file present?  The answer is again, you need to remember that these
are two different jobs which run on different Runners (separate virtual
machines).  The "update_dependence_graph" job starts on a fresh Runner created
from the ubuntu-latest runner image, and hence will not have the pom.xml file.
One way to solve this issue is to checkout the repository again within this
job as well, and that is a perfectly valid solution.  An alternative solution
is to pass on the checked out repository via shared storage between jobs
called GitHub Caches.

#### GitHub Caches

A cache is a temporary key-value storage on the cloud that can store file(s)
or folder(s) that are accessible by later jobs, as long as they have the
correct key.  Any cache entries that have not been accessed in over 7 days
are automatically deleted by GitHub.  Let's see what is in our current
cache:

<img alt="Current cache entries" src=img/maven_ci_caches.png>

We already have a cache entry!  Where did this come from?  Focus on this
part of the maven-ci.yml file:

```
    # This invokes the Setup Java JDK GitHub action:
    # https://github.com/marketplace/actions/setup-java-jdk
    - name: Set up JDK 11
      uses: actions/setup-java@v5
      with:
        java-version: '11'
        distribution: 'zulu'
        cache: maven
```

Note the entry "cache: maven".  To find out what it does, you can read the
following section on the Setup Java JDK action page:
https://github.com/marketplace/actions/setup-java-jdk#caching-packages-dependencies

Essentially, it caches all Maven package dependencies (the packages that Maven
downloads into ~/.m2/repository) and associates all those files with the key
"setup-java-${{ platform }}-${{ packageManager }}-${{ fileHash }}", where the
fileHash is the hash of the pom.xml file.  It makes sense to associate the
files with the hash of pom.xml since the package files would not change unless
pom.xml changes.  We are caching those files so that the next time a job sets
up Java with Maven caching, it can just restore the packages from the cache
instead of downloading them again from Maven Central.  Try manually running
the "Maven CI" workflow and you can confirm that is indeed what happens:

<img alt="setup-java cache restored" src=img/maven_ci_caches_restored.png>

One use of caches is to make job executions more efficient in this way.
Another use of caches is to communicate data between jobs and this second
use is what we will leverage to transfer the pom.xml file from our first
"maven_test" job to second "update_dependence_graph" job.  Do the following:

1. Add the below lines to maven-ci.yml at the end of the "maven_test" job:

   ```
   - name: Cache build
     uses: actions/cache@v5
     with:
       key: cached-build-${{github.sha}}
       path: .
   ```

   This action will store all the files in the current path (which will
include pom.xml as well as all source files and class files built under the
target/ folder) and associate them with the key
"cached-build-${{github.sha}}".  The ${{github.sha}} pre-defined GitHub
variable gives you the current commit hash.  Essentially, you are
associating the build with the commit that triggered the build, which makes
sense, right?

1. Add the below lines to maven-ci.yml at the beginning of the steps of the
   "update_dependence_graph" job:

   ```
   - name: Restore cached build
     uses: actions/cache@v5
     with:
       key: cached-build-${{github.sha}}
       path: .
   ```

   This action will be able to find the previously cached build files using
the same key so that they can be used for dependence graph analysis.

After making these changes, commit and push.  This will trigger a new Maven
CI workflow as before.  Peek inside the "update_dependence_graph" job and
you will see that it still fails, and the reason is because the cache entry
was not found:

<img alt="Maven CI workflow cache not found" src=img/maven_ci_workflow_8.png>

Was this because the cache entry was not properly created?  Nope, we do see
that exact cache entry with the key:

<img alt="cache-build cache entry is created" src=img/maven_ci_caches_2.png>

#### Job Dependencies

So then what happened?  Remember I said that jobs run in parallel by
default?  The "maven_test" job and the "update_dependence_graph" job run
concurrently so there is no synchronization between the cache write step
and the cache read step.  Depending on which job runs faster, the cache
might be there or might not be there (essentially a **race condition**).
In fact, the cache action saves the cache in a "Post" step that runs only
after all the other steps of "maven_test" are done, so in practice
"update_dependence_graph" almost always loses this race.  (By the way, if you
click "Re-run failed jobs" instead of pushing a new commit, the cache saved by
the earlier "maven_test" run will already be there, which hides the race.
That is why I asked you to commit and push.)  To
solve this issue, insert a needs entry to enforce a dependence on the
"maven_test" job:

```
  update_dependence_graph:

    needs: [maven_test]  # Enforces that maven_test runs first

    runs-on: ubuntu-latest
```

Again, commit and push maven-ci.yml, which will trigger the Maven CI
workflow.  If you peek inside the run, this is what it looks like now:

<img alt="Maven CI workflow with sequential jobs" src=img/maven_ci_workflow_9.png>

Note how now the "update_dependence_graph" job happens sequentially after
the "maven_test" job, solving our race condition.  But why is the job still
failing?  Let's take a peek inside the job:

<img alt="Maven CI workflow cache found" src=img/maven_ci_workflow_10.png>

As you can see, this time the cache is successfully restored so that isn't
the problem.  

#### Job Permissions

The issue is actually with resource accessibility:

<img alt="update_dependence_graph job failure: resource not accessible" src=img/maven_ci_workflow_11.png>

The issue is that the job needs write permissions to the repository to
update the dependence graph, but currently it is set up for only read
permissions.  Note these lines for the "update_dependence_graph" job:

```
    permissions:
      contents: read
```

If you expand the "Set up job" step in the job run, you can plainly see that
"GITHUB_TOKEN Permissions" only include read access for the Contents.  

<img alt="Maven CI workflow GITHUB_TOKEN permissions" src=img/maven_ci_workflow_12.png>

As a matter of security, it is good policy to have as restrictive
permissions as possible for any GitHub job.  Jobs often rely on 3rd party
actions and if those action scripts are compromised, the last line of
defense is the permissions.  By setting it to read-only, we can prevent
malicious action scripts from furtively updating parts of the repository.

In this case, we clearly need write permissions for this job, so let's
update the permissions as such:

```
    permissions:
      contents: write
```

Write permission always includes read permission as well.  This will again
trigger a Maven CI workflow and this time, the run is finally successful!

<img alt="Maven CI workflow success!" src=img/maven_ci_workflow_13.png>

The reason that I created a separate job for updating the dependence graph
was exactly because it needed write permissions and I didn't want to relax
the permission restrictions for the original "maven_test" job.

### Enable Dependabot

Now that the Maven CI workflow has run successfully, let's check that our
dependency graph is properly updated in our repository.  Go to the
"Insights" menu and then click on "Dependency graph", and you should be able
to see all dependencies defined in your pom.xml file and all your workflows:

<img alt="Dependency graph after upload" src=img/dependency_graph.png>

The Dependabot whose job is to scour these dependencies is not enabled by
default however, and you need to go into Settings (the "Advanced Security"
page, formerly called "Code security and analysis") to change that:

<img alt="Dependabot settings before changing" src=img/settings_dependabot_before.png>

Click "Enable" on all the buttons that have to do with Dependabot.  When you
click on Enable for "Dependabot version updates", you will be asked to
create ".github/dependabot.yml" with the following content:

```
# To get started with Dependabot version updates, you'll need to specify which
# package ecosystems to update and where the package manifests are located.
# Please see the documentation for all configuration options:
# https://docs.github.com/github/administering-a-repository/configuration-options-for-dependency-updates

version: 2
updates:
  - package-ecosystem: "maven" # See documentation for possible values
    directory: "/" # Location of package manifests
    schedule:
      interval: "weekly"
```

The only thing you need to do here is type "maven" for the package-ecosystem
(as seen above).  This configures Dependabot to run a weekly schedule to
check for updates and security vulnerabilities for dependent packages.
After enabling all options, the settings page should look like this:

<img alt="Dependabot settings after changing" src=img/settings_dependabot_after.png>

As soon as you commit dependabot.yml, Dependabot will kick into action and
within a few seconds creates several branches to patch up pom.xml to update
some package versions, and corresponding pull requests for those branches:

<img alt="Pull requests created by Dependabot" src=img/pull_requests.png>

Those orange dots beside each pull request mean that the Maven CI workflow
to verify that pull request is still running.  Soon enough those orange dots
will turn into green check marks if the CI tests pass.

Try clicking on the first pull request and you will see what I mean.  Scroll
down until you see the "Merge pull request" button, and above it you will
see that two successful checks have been performed, all part of our Maven CI
workflow:

<img alt="Pull request checks have all passed" src=img/pull_request_checks.png>

You will see something similar on any other pull requests.  Since Maven CI has
verified that the updated package dependencies did not break our build, we can
safely press the "Merge pull request" button on each pull request to merge its
branch into our main trunk.  After all is said and done, you should see all
the pull requests closed (how many pull requests you get depends on how many
dependencies happened to be out of date at the time):

<img alt="Pull requests closed after merging" src=img/pull_requests_closed.png>

Now we can be confident that all our dependent packages are up-to-date and
do not contain any known vulnerabilities.  Of course, new vulnerabilities are
discovered all the time, which is why Dependabot keeps checking every week.

Dependabot was instrumental in mitigating the damage due to the Log4j
(Log4Shell) vulnerability of December 2021 by [alerting thousands of
repositories and creating thousands of pull
requests](https://github.blog/2021-12-14-using-githubs-security-features-identify-log4j-exposure-codebase/).

### Add CodeQL TaaS Workflow

In the software industry, there are a number of companies that provide TaaS
(Testing-as-a-Service), a type of SaaS (Software-as-a-Service).  These services
typically perform static testing and code analysis which can be performed
without knowing the requirements of your software.  These services are easily
integrated into your CI/CD pipeline through the GitHub Marketplace.
We are going to practice integrating a code quality and security analysis tool
called CodeQL.

On your GitHub repository page:

1. Navigate to "Settings > Advanced Security".

1. Click on the "Explore workflows" button next to the Code Security > Tools >
Other tools setting.

1. The first workflow that pops up would be "CodeQL Analysis" by GitHub.  If it
is not, simply search for it.  Click on the "Configure" button for that
workflow.

1. In the .github/workflows/codeql.yml file generated, uncomment the following line:

   ```
   queries: security-extended,security-and-quality
   ```

   For a little bit of background, CodeQL is a code analysis engine developed
by GitHub (originally by Semmle, which was later acquired by GitHub). It
treats code as data in a database, upon which you can perform queries in a
domain specific language called CodeQL similar to SQL to find bugs and
security vulnerabilities.  By default, the workflow runs only CodeQL's default
query suite, which focuses on high-precision security queries.  Uncommenting
the above line adds the "security-extended" and "security-and-quality" suites,
which include additional security queries as well as code quality queries
(such as the "Missing Override annotation" issues you will see below).

1. Now click on "Commit changes".  This will trigger a new workflow due to the
push trigger on the YAML file.

When the workflow completes, navigate to "Security and quality > Code
scanning".  You should see a bunch of issues like the below:

<img alt="Results of code scanning" src=img/maven_ci_code_scanning.png>

I am not going to ask you to resolve all those issues as part of this exercise.
Just peruse each issue type and see what CodeQL is able to provide for you.

### Add Maven Publish Workflow

Now that we have a robust CI pipeline in place, we are ready to CD
(Continuous Delivery) as well.  We could choose to automatically publish our
package every time there is a code integration event, but typically software
organizations wait for a major release to redeploy.  The below YAML file
does exactly that:

```
name: Maven Package

on:
  workflow_dispatch:
  release:
    types: [created]

jobs:
  build:

    runs-on: ubuntu-latest

    permissions:
      contents: read
      packages: write

    steps:

    - name: Checkout repository
      uses: actions/checkout@v6

    - name: Set up JDK 11
      uses: actions/setup-java@v5
      with:
        java-version: '11'
        distribution: 'zulu'
        cache: maven

    - name: Build with Maven
      run: mvn -B package

    - name: Publish to GitHub Packages Apache Maven
      run: mvn -B deploy
      env:
        GITHUB_TOKEN: ${{ github.token }}
```

Create a new workflow file .github/workflows/maven-publish.yml using the above
text in exactly the same way you created maven-ci.yml previously.  Note that
this workflow is now triggered when a new release is created.  It uses the
`${{ github.token }}` to authenticate and publish the package.  The GitHub
pre-defined variable `${{ github.token }}` is automatically generated on each
workflow run and is given the permissions specified in the "permissions:"
entry.

You may wonder how "mvn deploy" knows to use this token.  Besides installing
the JDK, the setup-java action generates a Maven settings.xml file containing a
server entry whose id is "github" and whose credentials are read from the
GITHUB_ACTOR and GITHUB_TOKEN environment variables (you can see "Creating
settings.xml with server-id: github" in the log of that step).  That is why we
set the GITHUB_TOKEN environment variable for the publish step, and why the
\<id\> in the \<distributionManagement\> element below must be exactly
"github".

After the maven-publish.yml is committed and pushed, you also need to edit
the pom.xml file and add the below text after the \<scm\>...\</scm\> element:

```
  <distributionManagement>
    <repository>
      <id>github</id>
      <name>GitHub Apache Maven Packages</name>
      <url>https://maven.pkg.github.com/CS1632-Fall2026/cs-1632-fall-2026-exercise-8-ci-cd-pipelines-wonsunahn</url>
    </repository>
  </distributionManagement>
```

Replace the repository name within the \<url\>...\</url\> element with your own
repository name.  Please make sure you leave the
https://maven.pkg.github.com/CS1632-Fall2026/ part as-is and only change the
repository name, as https://maven.pkg.github.com/ is the base URL for the
GitHub Maven package registry.

You also need to find the artifactId and append your PittID to it so that it
becomes unique.  If your PittID has a capital letter please convert it to
lowercase (GitHub package names don't work with capital letters for some
reason).  For example, if your PittID was wahn (like mine), please change it
to:

```
<artifactId>cs1632-cicd-pipelines-wahn</artifactId>
```

Now we are ready to create a new release to see if this works!  Go to the
"<> Code" tab on your GitHub repository and then click on the "Create a new
release" link under the "Releases" section at the bottom right:

<img alt="Click on create new release" src=img/create_new_release_1.png>

Then click on "Choose a tag", and then type "v1.0" in the box and click on "+
Create new tag: v1.0 on publish".  This should create the "v1.0" tag.  Then
on the "Release title" box, write "Release v1.0".  Then click on the
"Publish release" button.  This should create your first release:

<img alt="Click on publish release" src=img/create_new_release_2.png>

This triggers the "Maven Package" workflow which publishes the package:

<img alt="Result of the Maven Package action" src=img/create_new_release_3.png>

In the end, you should see a new package registered on your github page:

<img alt="Package published on GitHub" src=img/create_new_release_4.png>

If you click on the package on the bottom right, you should see the contents
of your newly published package:

<img alt="Package details" src=img/create_new_release_5.png>

Note that GitHub Packages does not allow you to overwrite a package version
that has already been published.  If you create another release (or manually
run the workflow) without changing the \<version\> in pom.xml, the publish step
will fail with a "409 Conflict" error.  If you want to publish again, increment
the version in pom.xml first.


### Deploy Maven package and use in your Maven project

Let's try using the published Maven package by adding it as a dependency in a
Maven project as directed in the package page.  But before you are able to do
that, you need to add a **settings.xml** file that tells Maven that the GitHub
Maven package repository is one of the allowed repositories.  You need to place
the settings.xml file under the **.m2** folder under your home directory, which
is where Maven stores all its cached packages it downloads from Maven Central.
The .m2 folder is typically located in the following location.

If you are using Windows:

```
C:\Users\<YOUR_USER_NAME>\.m2
```

If you are using Mac:

```
/Users/<YOUR_USER_NAME>/.m2
```

If you are using Linux:

```
/home/<YOUR_USER_NAME>/.m2
```

Under the above folder add the settings.xml file with the following content:


```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/CS1632-Fall2026/*</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>USERNAME</username>
      <password>TOKEN</password>
    </server>
  </servers>
</settings>
```

Please replace USERNAME with your GitHub user name and replace TOKEN with a
Personal Access Token (PAT).  If you don't have a PAT (likely), you will need
to create one.  To generate a new token, on GitHub.com, go to Account >
Settings > Developer Settings > Personal Access Tokens > Tokens (classic) and
then click on the Generate New Token button and then the classic option.  Leave
a Note to yourself to help you remember what this token is for.  Also click on
the "repo" and "write:packages" checkboxes.  For the purposes of deploying the
package, you only need "read:packages" permissions really, but we will use this
token in Part 2 for other purposes too.  If you forgot the PAT string, you will
have to generate the token by clicking on "Regenerate Token" button.

After this you are ready to deploy the Maven package on a Maven project.
Let's try doing this on the Exercise 2 project from which we copied the
RentACat source code.  Open your Exercise 2 project on VSCode, and then copy
the dependency text suggested on the package page of our published package and
paste it at the beginning of the \<dependencies\> section, so that the classes
from your package take precedence.  Now, try deleting all the implementation
code under the src/main/java/edu/pitt/cs folder.  You should not get any
compilation errors since now all those classes are supplied by your package.
Then try clicking on the VSCode Testing extension and running all the tests.
This time, most of your tests will fail!  That is expected: the package you
published was built from the scaffolding code in your Part 1 repository, where
RentACatImpl and CatImpl are not implemented yet and the MOCK cases in Cat and
RentACat return null.  The failures are in fact proof that the classes are now
coming from your package rather than from your own Exercise 2 source code.
Now, please do not commit the code since you probably still want your
Exercise 2 source code.  You can discard the changes after you are satisfied.

### Submission

Before you submit, add a file named github_url.txt at the root of your Part 1
repository.  The file should contain a single line, which is the GitHub URL of
the Classroom50 repository you are submitting.  The URL is formatted as
follows:

```
https://github.com/CS1632-Fall2026/{your repository name}/
```

For example, mine would be:

```
https://github.com/CS1632-Fall2026/cs-1632-fall-2026-exercise-8-ci-cd-pipelines-wonsunahn/
```

Make sure you commit and push github_url.txt to your repository before
submitting.

When you are done, submit your repository to GradeScope at the
**Exercise 8 Part 1** link.  Once you submit, GradeScope will run the
autograder to grade you and give feedback.  If you get deductions, fix your
code based on the feedback and resubmit.  Repeat until you don't get
deductions.

## Part 2: Dockers

In Part 2, we will use Docker to test and deploy the Rent-A-Cat website that we
tested in Deliverable 3.  We will test the website using the Playwright tests
that you wrote for the assignment.

Docker runs software in a self-contained virtualized environment called
containers.  A container is launched from a Docker image, which is a binary
file that contains the file system that the container is launched from.  You
can think of Docker images as comparable to virtual machine disk images,
except that they contain only the OS userland (libraries, tools, and files)
and not an OS kernel: containers share the kernel of the host machine.  Docker
images can be built from any operating system version supported by Docker and
can come with any software or files pre-installed.  As such, Docker images are
the preferred method of deployment for many software organizations.  A Docker
image can run reliably on any user machine or on a cloud service provider
without a hitch since everything the software needs can come pre-packaged.

Deploying software encapsulated in a Docker image makes testing simpler and
more rigorous at the same time. Now the tester does not have to think about
myriad preconditions that can impact the software such as operating system
versions, whether libraries and packages of certain version are installed,
or whether environment variables and configuration files are set with
correct values.  And these preconditions are usually what causes software to
fail during deployment.

### Create Classroom50 Repository

Please use the link for Part 2 posted on the Teams Exercise 8 channel to
accept this exercise and create your repository.

### Prerequisites

1. Download and install Docker Desktop to be able to launch Docker containers:
   https://www.docker.com/products/docker-desktop/

1. Make sure you have Node.js, a JDK, and Maven installed on your machine (you
   should already have them from Exercise 2 and Deliverable 3).

1. Install the Playwright Test package as a dev dependency in the Node.js
   project:

   ```
   npm install -D @playwright/test
   ```

1. Install chromium as the Playwright test browser:

   ```
   npx playwright install --with-deps chromium
   ```

### Do some sanity tests

This project contains a Spring Boot application, which is a Java framework
for creating web servers.  First, let's start by launching the web server
and making sure it is working.  You can launch using the following command:

```
mvn spring-boot:run
```

The output from this command should end in these two lines:

```
...
2026-03-25 18:33:15.380  INFO 21180 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2026-03-25 18:33:15.395  INFO 21180 --- [           main] c.s.ServingWebContentApplication         : Started ServingWebContentApplication in 2.46 seconds (JVM running for 2.852)
```

Note this starts the Tomcat web server listening on port 8080.  Try opening
the web app on a browser using the URL:

```
http://localhost:8080/
```

You should see our Rent-A-Cat website.

Now leave the server running and open another terminal to invoke Playwright test:

```
npx playwright test
```

There is a single Playwright test in tests/rentacat.spec.ts in this project
that tests that the web server can service HTTP requests on port 8080:

```
test('TEST-CONNECTION', async ({ page }) => {
  await page.goto(baseURL);
});
```

And of course, it should pass.

### Create Docker image

We want to deploy our web app as a Docker image.  Creating a Docker image is
much simpler than you think!  You simply start from a base Linux image and
then "layer" changes on top of it to create your custom image.  You
write this process into something called a Dockerfile and you are done.

Create a Dockerfile at the root of your repository with the following content:

```
# Base image from https://hub.docker.com/layers/library/maven/3.9.14-eclipse-temurin-11-noble/
FROM maven:3.9.14-eclipse-temurin-11-noble

# define working directory
WORKDIR /app

# copy over app files
COPY pom.xml .
COPY src src

# expose default Spring Boot port 8080
EXPOSE 8080

# define default command
CMD ["/bin/sh", "-c", "mvn spring-boot:run"]
```

The description of the base image maven:3.9.14-eclipse-temurin-11-noble can be
found here:
https://hub.docker.com/layers/library/maven/3.9.14-eclipse-temurin-11-noble/

If you navigate to the above URL, you will see that a Docker image is
constructed in layers, starting from the base operating system layer (note
that a Docker image does not include an OS kernel; containers share the kernel
of the host machine).  Each command that copies files or installs software
packages creates a new layer on top of the previous layers.  This layered
structure [allows one or more layers to be shared between multiple images,
improving storage
efficiency](https://docs.docker.com/get-started/docker-concepts/building-images/understanding-image-layers/).
As such, our image builds several layers on top of the base image, so if there
was another image using the same base image, the base image layers would be
shared.

For our image, we start with a base image with the Ubuntu 24.04 LTS OS
(that's what "noble" means) preinstalled with Maven and Temurin JDK 11.  On top
of it we copy over files required to launch our web app to the work directory
specified as /app.  We also expose TCP port 8080 to the outside world since
that is the port that Spring Boot is going to be using.  If we don't explicitly
expose ports, they will not be accessible in the Docker container created out
of this image.  Lastly, we define the command that will be executed by default
when the image is launched in a container, which is "mvn spring-boot:run".

Note that our image is not entirely self-contained.  "mvn spring-boot:run"
downloads the Spring Boot dependencies from Maven Central every time the
container starts, so the container needs Internet access and takes a while for
the web server to come up (keep this in mind, as it will become important
later!).  A production image would typically download the dependencies, or
build the application JAR file, at image build time instead.

Third party base images for popular Linux distributions and CPU architectures
preinstalled with a variety of software packages can be found at [Docker
Hub](https://hub.docker.com/search).

### Create Docker Container

Now let's try creating a Docker container out of this image to test it.  By
passing the "compose" argument to the docker tool, we can compose one or more
Docker containers and network them together into a distributed system.  It is
configured using another file in YAML format named docker-compose.yaml.  In our
case, we just have one container so it is rather simple:

```
services:
  server:
    build:
      context: '.'
      dockerfile: Dockerfile
    container_name: rentacat
    ports:
      - "8080:8080"
```

Add a docker-compose.yaml file with the above content at the root of the
repository.  The file consists of a list of services that are launched
together.  In our case, we just have one service named "server".  A service
can be created from a Docker image pulled from Docker Hub or some other
registry, or, as in our case, built locally when the "build:" keyword is
specified.  The "context:" and "dockerfile:" values specify the context
within which the Docker image will be built and the name of the Dockerfile.
The "ports:" values specify a port mapping between container and host --- in
this case, port 8080 is mapped to the same port on the host.

Now we are going to use this YAML file to launch a container listening on
port 8080.  But before doing so, we need to kill the web server that we
launched previously or we are going to have a port conflict.  Go to the
terminal where Spring Boot is running and kill the process using Ctrl+C.
Make sure the server is dead by reloading the page http://localhost:8080/ on
your web browser and confirming that the server is not found.

Now let's first start Docker Desktop, which will start Docker Engine
included in the application.  After it is running, invoke
"docker compose up" to bring up the container:

```
docker compose up
```

You should soon see the image registered on Docker Desktop:

<img alt="Docker Desktop Images" src=img/docker_1.png>

And also a container instance running with the name "rentacat" with port
8080 open:

<img alt="Docker Desktop Containers" src=img/docker_2.png>

Now try reloading http://localhost:8080/ again and you should see the page
back up.  Congrats, you have created your first Docker container!

To stop the container, you only need to click on the trash bin icon that
appears when you hover over the container.  Or you can do so on the command
line:

```
docker compose down
```

You may also go to the "Images" menu and delete the image if you wish to do
so.  As long as the image is still there, you can relaunch the container
using the "Run" button, but when you do, make sure that you open the
"Optional Settings" and enter the port mapping 8080 on Local Host:

<img alt="Port mapping to 8080" src=img/docker_3.png>

### Import Playwright Tests

If you have stopped the container, fire up the container again because we
are going to write some tests for it.  We want to add some actual Playwright
tests that test the web app.  Well, we already wrote those tests for
Deliverable 3, so let's just import rentacat.spec.ts from that project into
tests/rentacat.spec.ts.

You need to do these two things though in rentacat.spec.ts:

1. Replace the base URL of the web pages accessed with "http://localhost:8080".
Please make sure you use http:// and not https://.

1. Remove the tests that fail because they trigger defects on the web app
   (remember the tests whose names start with DEFECT?).

Make sure everything passes with:

```
npx playwright test
```

Detailed test results are under the playwright-report/ folder.

### Add CI Test Workflow

Now let's automate the test environment setup for the web app and the testing
itself in a CI pipeline.  Add a new workflow file named
.github/workflows/docker-ci.yml to your GitHub repository with the following
content (if you forgot how to do that already, review the [instructions for
Maven CI](#add-maven-ci-workflow)):

```
name: Docker CI

on:
  workflow_dispatch:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:

  test_dockerized_webserver:

    runs-on: ubuntu-latest

    permissions:
      contents: read

    steps:

      - name: Checkout repository
        uses: actions/checkout@v6

      - name: Set up Playwright
        run: npm install -D @playwright/test
      
      - name: Set up Playwright Browsers
        run: npx playwright install --with-deps chromium

      - name: Setup Docker buildx
        uses: docker/setup-buildx-action@v4

      - name: Launch Web Service
        run: docker compose up -d

      - name: Run Playwright Tests
        run: npx playwright test --project=chromium

        # https://github.com/marketplace/actions/upload-a-build-artifact
        # Upload even if the tests fail, so that you can inspect the failures
      - name: Upload Playwright results as artifact
        if: ${{ !cancelled() }}
        uses: actions/upload-artifact@v6
        with:
          name: Playwright results
          path: playwright-report/

```

The steps are a replica of what we just did manually.  Note the "if:"
condition on the last step.  By default, a step is skipped if any previous step
in the job failed, but we want the Playwright report precisely when the tests
fail!  The condition tells GitHub to run the upload step unless the workflow
was cancelled.

Commit and push all these changes and the Docker CI workflow will trigger
immediately.  Now, the Playwright tests may pass but it may also fail.  If you
run it a few times, you may notice that it sometimes passes and sometimes
fails, and when it fails, it fails in different ways.  Here is an example of a
failure (stress on example, since you will likely see a different failure):

```
    Retry #1 ───────────────────────────────────────────────────────────────────────────────────────

    Error: page.goto: net::ERR_CONNECTION_RESET at http://localhost:8080/
    Call log:
      - navigating to "http://localhost:8080/", waiting until "load"


      24 |
      25 | test('TEST-3-LISTING', async ({ page }) => {
    > 26 |   await page.goto(baseURL);
         |              ^
        at /home/runner/work/CS1632_CICD_Docker_Solution/CS1632_CICD_Docker_Solution/tests/rentacat.spec.ts:26:14

    Error Context: test-results/rentacat-TEST-3-LISTING-chromium-retry1/error-context.md

    attachment #2: trace (application/zip) ─────────────────────────────────────────────────────────
    test-results/rentacat-TEST-3-LISTING-chromium-retry1/trace.zip
    Usage:

        npx playwright show-trace test-results/rentacat-TEST-3-LISTING-chromium-retry1/trace.zip

    ────────────────────────────────────────────────────────────────────────────────────────────────

  2 failed
    [chromium] › tests/rentacat.spec.ts:5:5 › TEST-1-RESET ─────────────────────────────────────────
    [chromium] › tests/rentacat.spec.ts:19:5 › TEST-2-CATALOG ──────────────────────────────────────
  1 flaky
    [chromium] › tests/rentacat.spec.ts:25:5 › TEST-3-LISTING ──────────────────────────────────────
  8 passed (22.9s)
Error: Process completed with exit code 1.
```

Some tests fail due to net::ERR_CONNECTION_RESET and some due to other reasons.
What's happening?  The fact that the test results are nondeterministic tells
you that it is due to either memory errors or race conditions (the two sources
of nondeterminism by mistake we learned in class).  It is definitely not a
memory error since neither Java (the language the web server is written in) nor
TypeScript (the language the Playwright tests are written in) allow memory
errors to happen.  So it must be a **race condition**, and you would be right.

Let's go back to our docker-ci.yml file.  The step "docker compose up -d"
includes an option "-d" that we didn't use before.  The "-d" option is short
for "detached" and allows docker compose to execute detached from the
terminal so that the commandline can immediately return and continue
executing the next steps (you can try it yourself on a terminal if you wish
to).  That means that by the time you get to the Playwright tests, the
container may still be in the middle of launch.  Or, even if the container
is up, the web server may not be ready yet.

Again, to solve this issue, we have to put in some kind of synchronization
to avoid the race condition.  The form of synchronization will necessarily
differ depending on the type of service you are waiting to be ready (e.g.
checking that a web server is ready will look different from checking that a
database server is ready).  I wrote a custom script for you to wait for the
web server:

```
#!/bin/sh

set -e
  
until curl http://localhost:8080/; do
  >&2 echo "Web service is unavailable - sleeping"
  sleep 1
done
  
>&2 echo "Webservice is up - continuing"
```

The curl command line tool fetches the page from the given URL and prints it
on the screen.  More important for our purposes, it returns an exit code of
0 if successful and a non-zero value if not (just like most Linux tools).
So the script will poll the URL every second until it can fetch the page.

Save the above script to a file named "wait-for-webserver.sh" at the root of
your repository.  And insert the invocation of that script right before
running the Playwright tests.  I'll leave it to you to name the step however
you want.  A few tips:

* Invoke the script with "sh wait-for-webserver.sh" rather than
  "./wait-for-webserver.sh".  The latter requires the file to have execute
  permission in Git, which is often lost when the file is committed from
  Windows.

* Make sure the file has Unix (LF) line endings.  A shell script saved with
  Windows (CRLF) line endings will fail on the Runner with strange errors.
  Adding the line "*.sh text eol=lf" to your .gitattributes file prevents this.

* Consider adding "timeout-minutes: 5" to the step.  If the web server never
  comes up for some reason, the script would otherwise loop until GitHub
  kills the job at its 6-hour limit.

After you push that change, try launching the workflow several times.  Now,
you will see the workflow reliably succeeding.  In fact, if you peek into a
workflow and look into the "wait-for-webserver.sh" step, you will see the
script waiting for the web server to start up:

<img alt="Waiting for web server to start" src=img/wait_for_webserver_1.png>

Only when the web server responds with a page does the script return and
allow the workflow to continue on to Playwright testing:

<img alt="Web server is up and the workflow continues" src=img/wait_for_webserver_2.png>

Just like for Part 1, the Docker CI workflow stores Playwright test results as
an artifact visible at the bottom of the Summary page for a workflow run:

<img alt="Docker artifact" src=img/docker_artifact.png>

Try downloading it and opening the index.html file in a browser.

### Add Docker Publish Workflow

Now that we have CI tests all set up and passing, it is time to create the
delivery workflow.  Create a new workflow file
.github/workflows/docker-publish.yml with the following content:

```
name: Docker Publish

on:
  workflow_dispatch:
  release:
    types: [created]

env:
  # ghcr.io is the Docker registry maintained by GitHub
  REGISTRY: ghcr.io
  # github.repository as <account>/<repo>
  IMAGE_NAME: ${{ github.repository }}


jobs:
  build:

    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
      - name: Checkout repository
        uses: actions/checkout@v6

      - name: Setup Docker buildx
        uses: docker/setup-buildx-action@v4

      # Login against a Docker registry
      # https://github.com/docker/login-action
      - name: Log into registry ${{ env.REGISTRY }}
        uses: docker/login-action@v4
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      # Extract metadata (tags, labels) for Docker
      # https://github.com/docker/metadata-action
      - name: Extract Docker metadata
        id: meta
        uses: docker/metadata-action@v6
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}

      # Build and push Docker image with Buildx
      # https://github.com/docker/build-push-action
      - name: Build and push Docker image
        id: build-and-push
        uses: docker/build-push-action@v7
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
          platforms: linux/amd64,linux/arm64,linux/arm/v7
```

The workflow publishes the web server Docker image to ghcr.io (the GitHub
Docker image registry).  The docker/build-push-action@v7 GitHub action does
all the heavy lifting.  The "platforms:" entry builds the image for multiple
CPU architectures, so that the same image can run on Intel/AMD machines
(linux/amd64) as well as ARM machines such as Apple Silicon Macs
(linux/arm64).

Note that, unlike the Maven Publish workflow (where "mvn deploy" runs the
JUnit tests as part of the Maven lifecycle), this workflow publishes the image
without running the Playwright tests.  In a real CI/CD pipeline, you would
want to gate delivery on the tests passing, for example by adding the
"test_dockerized_webserver" job to this workflow and making the "build" job
depend on it using "needs:".

Once this workflow is committed and pushed, trigger it by creating a new
release on the "<> Code" tab.  Add a tag by clicking on the "Choose a tag" drop
down, such as "v1.2" (since that is the version number in the pom.xml file).
After the workflow completes, go to the "<> Code" tab and you should see a new
package in the Packages section on the bottom right.  If you click on the
package link, you should see something like the below:

<img alt="Published Docker package" src=img/docker_publish.png>

### Pull published Docker image and launch from desktop

If your package is private, you need to authenticate to the GitHub Container
Registry before pulling the package (if it is public, logging in does no
harm).  You can use the PAT (Personal Access Token) that you generated
previously in Part 1 for this purpose.  If you have not yet generated the
token, please refer to the [Deploy Maven package and use in your Maven
project](#deploy-maven-package-and-use-in-your-maven-project) section in
Part 1.

Now on the commandline do:

```
docker login ghcr.io -u <github_username>
```

It is going to ask for your password and this is where you provide your PAT.
This is what the interaction should look like:

```
$ docker login ghcr.io -u wonsunahn
Password: 
Login Succeeded
```

Next, copy the "Install from the command line" text from your GitHub package
page, which was in my case:

```
docker pull ghcr.io/cs1632-fall2026/cs-1632-fall-2026-exercise-8-dockers-wonsunahn:v1.2
```

Note that Docker image names must be all lowercase, even though the GitHub
organization name has capital letters.  The part after the colon is the image
tag.  Since we triggered the workflow by creating a release, the image is
tagged with the release tag (v1.2 in my case) as well as "latest".  If you had
instead run the workflow manually on the main branch, the image would have been
tagged "main" (as in the screenshot above).

Then run that command on the terminal.  This will pull the published image
on to your Docker Desktop.  If you check the "Images" menu, you will see a new
image created:

<img alt="Published Docker image pulled" src=img/docker_4.png>

Try removing the container you created from your locally built image (if you
haven't already), and then run the published image (taking care that you map
port 8080 in "Optional Settings").  Now if you load http://localhost:8080/ on
your browser, it should work as expected.

### (Optional) Run Maven CI inside a Container

This subsection is optional and will not be graded, but I encourage you to try
it since it ties Part 1 and Part 2 together.  In Part 1, our Maven CI workflow
ran directly on the Runner virtual machine and used the setup-java action to
install JDK 11 on it.  GitHub Actions also lets you run a job's steps inside a
Docker container on the Runner, using the "container:" keyword.  Let's try
that with the same base image that we used in our Dockerfile.

Go back to your Part 1 repository and add a new workflow file named
maven-ci-container.yml with the following content:

```
name: Maven CI Container

on:
  workflow_dispatch:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:

  # Runs the Maven test phase on the project inside a container
  maven_test_container:

    runs-on: ubuntu-latest

    # Run all the steps inside this container instead of directly on the
    # Runner virtual machine.  This is the same base image as our Dockerfile.
    container: maven:3.9.14-eclipse-temurin-11-noble

    permissions:
      contents: read

    steps:

    - name: Checkout repository
      uses: actions/checkout@v6

    # Note there is no "Set up JDK 11" step: the image already comes with
    # Temurin JDK 11 and Maven preinstalled.
    - name: Test with Maven
      run: mvn -B test

    - name: Upload jacoco results as artifact
      uses: actions/upload-artifact@v6
      with:
        name: Jacoco coverage results (container)
        path: target/site/jacoco
```

Commit and push, and both the original Maven CI workflow and the new Maven CI
Container workflow will be triggered.  Peek inside the maven_test_container
job of the new workflow and you will see an extra "Initialize containers" step
where the Runner pulls the image and starts the container, and you will see
that the "Set up JDK 11" step is gone.  You may also want to try adding a step
that runs "mvn -v" to both workflows and compare the Maven and Java versions
that each reports.

So what did we gain?  First, **control of the environment**.  In maven-ci.yml,
the job starts from GitHub's runner image and we set up only the pieces we
explicitly asked for (JDK 11).  Everything else, such as the OS release,
system libraries, the Maven version, and the hundreds of other preinstalled
tools, is whatever GitHub ships in the runner image, and that changes over
time.  In maven-ci-container.yml, the entire environment is defined by an
image that we chose.  It is the same image we used in our Dockerfile, and the
same image you can run on your own laptop using Docker Desktop, so the
environment in which CI runs your tests can be identical to the one on your
machine.

Second, **speed and reliability of setup**.  Prebuilt images replace setup
steps that install software on every run, and each of those steps takes time
and can fail (for example, when a download server is down).  However, for this
particular example, the bare virtual machine is likely competitive or even
faster.  The setup-java action is quick and its "cache: maven" option restores
the Maven dependencies from the GitHub cache.  On the other hand, the
container job has to pull the image on every run (remember, the Runner is a
virtual machine newly created for each job), and since it does not use
setup-java's caching, Maven downloads all the dependencies again.  The
equation changes as the setup gets more complex.  If your job needs many
tools, specific versions of system packages, or libraries that must be
compiled from source, a single image pull can replace a long list of slow and
fragile installation steps.

### Submission

Before you submit, add a file named github_url.txt at the root of your Part 2
repository.  The file should contain a single line, which is the GitHub URL of
the Classroom50 repository you are submitting.  The URL is formatted as
follows:

```
https://github.com/CS1632-Fall2026/{your repository name}/
```

For example, mine would be:

```
https://github.com/CS1632-Fall2026/cs-1632-fall-2026-exercise-8-dockers-wonsunahn/
```

Make sure you commit and push github_url.txt to your repository before
submitting.

When you are done, submit your repository to GradeScope at the
**Exercise 8 Part 2** link.  Once you submit, GradeScope will run the
autograder to grade you and give feedback.  If you get deductions, fix your
code based on the feedback and resubmit.  Repeat until you don't get
deductions.
