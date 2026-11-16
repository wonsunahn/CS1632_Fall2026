**Note: the items in bold are learning goals that require you to apply your
knowledge to a software QA scenario, rather than simply recalling knowledge,
and hence may need more preparation.**

## CI/CD PIPELINES
* Be explain what Continuous Integration (CI) and Continuous Delivery (CD)
* pipelines are why they are needed.
* Be able to define the parts of a GitHub Actions workflow: workflow, job,
  step, runner, and action (the "uses:" keyword).
* Be able to explain what triggers a workflow (push, pull_request,
  workflow_dispatch, release) and when each is appropriate.
* Be able to explain why GitHub-hosted runners are virtual machines that are
  newly created for each job, and what that implies for sharing files between
  jobs.
* Be able to explain why jobs run in parallel by default and how "needs"
  enforces an ordering between jobs.
* Be able to explain how a CI job determines pass / fail (exit codes) and why
  artifacts are needed to publish results beyond the green check mark.
* Be able to explain the principle of least privilege for job permissions
  why it is a defense against compromised third-party actions.
* Be able to explain why package versions are immutable in a package registry
  and what that means for publishing a new release.

## DOCKER CONTAINERS
* Be able to explain the difference between a Docker image and a Docker container.
* Be able to explain what each Dockerfile instruction does (FROM, WORKDIR,
  COPY, EXPOSE, CMD) and what docker compose adds on top of a Dockerfile.
* Be able to explain why port mapping is needed to reach a service running
  inside a container.
* Be able to explain how Docker images simplify testing and deployment by
  packaging the execution environment along with the software.
* Be able to explain the advantages of running CI jobs inside a container on a
  virtual machine (control of the environment, reproducibility, speed and
  reliability of setup), and when a bare virtual machine may be competitive.
