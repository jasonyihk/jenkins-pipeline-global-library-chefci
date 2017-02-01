package org.typo3.chefci.v2.cookbook.stages

import org.typo3.chefci.helpers.JenkinsHelper
import org.typo3.chefci.helpers.Slack
import org.typo3.chefci.v2.shared.stages.AbstractStage

class Lint extends AbstractStage {

    Lint(Object script, JenkinsHelper jenkinsHelper, Slack slack) {
        super(script, 'Lint', jenkinsHelper, slack)
    }

    @Override
    void execute() {
        script.stage(stageName) {
            foodcritic()
            rubocop()
        }
    }

    private foodcritic(){
        script.node {
            // we have to manually disable these directories because foodcritic is stupid by default.
            // https://github.com/acrmp/foodcritic/issues/148
            script.sh('foodcritic . --exclude spec --exclude test')
        }
    }

    private rubocop(){
        script.node {
            // see also http://atomic-penguin.github.io/blog/2014/04/29/stupid-jenkins-and-chef-tricks-part-1-rubocop/
            script.sh('rubocop --fail-level E')
            script.step([$class: 'WarningsPublisher', canComputeNew: false, canResolveRelativePaths: false, consoleParsers: [[parserName: 'Foodcritic'], [parserName: 'Rubocop']], defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', unHealthy: ''])
            script.step([$class: 'AnalysisPublisher'])
        }
    }

}